package com.by5388.learn.v4.kotlin.photogallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class PhotoGalleryViewModel : ViewModel() {
    val mGalleryItemLiveData: LiveData<List<GalleryItem>>
    private val mFlickrFetchr = FlickrFetchr()

    init {
        mGalleryItemLiveData = mFlickrFetchr.fetchPhotos()
    }

    override fun onCleared() {
        super.onCleared()
        mFlickrFetchr.cancelRequestInFlight()
    }
}