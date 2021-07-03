package com.by5388.learn.v4.kotlin.photogallery

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.by5388.learn.v4.kotlin.photogallery.api.FlickrApi
import com.by5388.learn.v4.kotlin.photogallery.api.FlickrResponse
import com.by5388.learn.v4.kotlin.photogallery.api.PhotoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "FlickrFetchr"

class FlickrFetchr {
    private val mFlickrApi: FlickrApi

    /**
     * 网络请求
     */
    private val mPhotoRequestList: MutableList<Call<FlickrResponse>> = mutableListOf()
    //https://api.flickr.com/services/rest/?method=flickr.interestingness.getList&api_key=d80e0fbab7551485a80a02a521f228eb&format=json&nojsoncallback=1&extras=url_s

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            //.addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        mFlickrApi = retrofit.create(FlickrApi::class.java)
    }

    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()
        val flickrRequest: Call<FlickrResponse> = mFlickrApi.fetchPhotos()
        mPhotoRequestList.add(flickrRequest)
        flickrRequest.enqueue(object : Callback<FlickrResponse> {
            override fun onResponse(
                call: Call<FlickrResponse>,
                response: Response<FlickrResponse>
            ) {
                Log.d(TAG, "onResponse: ")
                val flickrResponse: FlickrResponse? = response.body()
                val photoResponse: PhotoResponse? = flickrResponse?.photos
                var galleryItems: List<GalleryItem> = photoResponse?.galleryItems ?: mutableListOf()
                //todo 20210703
                galleryItems = galleryItems.filterNot {
                    it.url.isBlank()
                }

                responseLiveData.value = galleryItems
                mPhotoRequestList.remove(flickrRequest)
            }

            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ", t)
                mPhotoRequestList.remove(flickrRequest)
                //被取消
                if (flickrRequest.isCanceled){
                    Log.d(TAG, "onFailure: 任务撤销成功")
                }
            }
        })
        return responseLiveData
    }

    fun cancelRequestInFlight() {
        Log.d(TAG, "cancelTask: ")
        for (request in mPhotoRequestList) {
            request.cancel()
        }
        mPhotoRequestList.clear()
    }
}