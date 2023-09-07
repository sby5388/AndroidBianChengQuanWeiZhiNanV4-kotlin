package com.by5388.learn.v4.kotlin.photogallery

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.by5388.learn.v4.kotlin.photogallery.api.PhotoResponse
import com.by5388.learn.v4.kotlin.photogallery.navigation.PhotoGalleryNavFragment.Companion.SEARCH_TYPE_COROUTINES
import com.by5388.learn.v4.kotlin.photogallery.navigation.PhotoGalleryNavFragment.Companion.SEARCH_TYPE_RETROFIT
import com.by5388.learn.v4.kotlin.photogallery.navigation.PhotoGalleryNavFragment.Companion.SEARCH_TYPE_RXJAVA
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoGalleryViewModel(application: Application) : AndroidViewModel(application) {
    val mGalleryItemLiveData: LiveData<List<GalleryItem>>
    private val mFlickrFetchr = FlickrFetchr()
    private val mMutableLiveDataSearchTerm: MutableLiveData<String> = MutableLiveData()
    private var mSearchType: Int = 0
    private val mDispose = CompositeDisposable()

    val mSearchTerm: String
        get() = mMutableLiveDataSearchTerm.value ?: ""


    init {
        mMutableLiveDataSearchTerm.value = QueryPreferences.getStoredQuery(getApplication())
        // TODO: 2021/7/15  Transformations.switchMap ??
        mGalleryItemLiveData = Transformations.switchMap(mMutableLiveDataSearchTerm) { searchTerm ->
            //mFlickrFetchr.searchPhotos(searchTerm)
            loadResult(searchTerm, mSearchType)
        }
    }

    /**
     * 搜索图片，默认参数为空
     */
    fun fetchPhotos(query: String = "", searchType: Int = SEARCH_TYPE_RETROFIT) {
        Log.d(TAG, "fetchPhotos:  query = $query type = $searchType")
        mSearchType = searchType
        QueryPreferences.setStoredQuery(getApplication(), query)
        //2021/7/15 这里会触发  mFlickrFetchr.searchPhotos(searchTerm)
        mMutableLiveDataSearchTerm.value = query
    }

    override fun onCleared() {
        super.onCleared()
        mDispose.dispose()
        mFlickrFetchr.cancelRequestInFlight()
    }


    private fun loadResult(text: String, type: Int): LiveData<List<GalleryItem>> {
        return when (type) {
            SEARCH_TYPE_RETROFIT -> text.let { mFlickrFetchr.searchPhotos(it) }
            SEARCH_TYPE_RXJAVA -> loadResultRx(text)
            SEARCH_TYPE_COROUTINES -> loadResultSuspend(text)
            else -> throw java.lang.RuntimeException("非法参数")
        }
    }

    private fun loadResultRx(text: String): LiveData<List<GalleryItem>> {
        Log.d(TAG, "loadResultRx: ")
        return MutableLiveData<List<GalleryItem>>().apply {
            val disposable = mFlickrFetchr.mFlickrApi.searchPhotosRx(text)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ t -> value = t?.galleryItems }
                ) { value = null }
            mDispose.add(disposable)
        }
    }


    private fun loadResultSuspend(text: String): LiveData<List<GalleryItem>> {
        Log.d(TAG, "loadResultSuspend: ")
        return MutableLiveData<List<GalleryItem>>().apply {
            viewModelScope.launch(Dispatchers.Main) {
                val response: PhotoResponse = mFlickrFetchr.mFlickrApi.searchPhotosSuspend(text)
                value = response.galleryItems
            }
        }
    }

    companion object {
        const val TAG = "PhotoGalleryViewModel"
    }


}