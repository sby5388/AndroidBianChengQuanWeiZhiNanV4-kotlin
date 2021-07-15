package com.by5388.learn.v4.kotlin.photogallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

class PhotoGalleryViewModel(application: Application) : AndroidViewModel(application) {
    val mGalleryItemLiveData: LiveData<List<GalleryItem>>
    private val mFlickrFetchr = FlickrFetchr()
    private val mMutableLiveDataSearchTerm: MutableLiveData<String> = MutableLiveData()

    val mSearchTerm: String
        get() = mMutableLiveDataSearchTerm.value ?: ""


    init {
        mMutableLiveDataSearchTerm.value = QueryPreferences.getStoredQuery(getApplication())
        // TODO: 2021/7/15  Transformations.switchMap ??
        mGalleryItemLiveData = Transformations.switchMap(mMutableLiveDataSearchTerm) { searchTerm ->
            mFlickrFetchr.searchPhotos(searchTerm)
        }
    }

    /**
     * 搜索图片，默认参数为空
     */
    fun fetchPhotos(query: String = "") {
        QueryPreferences.setStoredQuery(getApplication(), query)
        // TODO: 2021/7/15 这里会触发  mFlickrFetchr.searchPhotos(searchTerm)
        mMutableLiveDataSearchTerm.value = query
    }

    override fun onCleared() {
        super.onCleared()
        mFlickrFetchr.cancelRequestInFlight()
    }
}