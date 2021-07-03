package com.by5388.learn.v4.kotlin.photogallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class PhotoGalleryViewModel : ViewModel() {
    val mGalleryItemLiveData: LiveData<List<GalleryItem>>

    init {
        mGalleryItemLiveData = FlickrFetchr().fetchPhotos()
    }

}