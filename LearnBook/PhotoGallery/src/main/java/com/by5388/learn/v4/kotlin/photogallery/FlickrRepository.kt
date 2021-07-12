package com.by5388.learn.v4.kotlin.photogallery

import android.app.Application
import androidx.lifecycle.LiveData

class FlickrRepository private constructor(application: Application) {
    private val mFlickrApi = FlickrFetchr()

    fun getLiveDataPhotoList(): LiveData<List<GalleryItem>> {
        return mFlickrApi.fetchPhotos()
    }

    fun cancelRequestInFlight() {
        mFlickrApi.cancelRequestInFlight()
    }

    companion object {
        private var INSTANCE: FlickrRepository? = null
        fun initialize(application: Application) {
            if (INSTANCE == null) {
                INSTANCE = FlickrRepository(application);
            }
        }

        fun get(): FlickrRepository {
            return INSTANCE ?: throw IllegalStateException("FlickrRepository must be initialized")
        }
    }

}