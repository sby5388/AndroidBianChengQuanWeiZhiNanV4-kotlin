package com.by5388.learn.v4.kotlin.photogallery.base

import com.by5388.learn.v4.kotlin.photogallery.GalleryItem

interface GalleryClick {
    fun onGalleryClick(item: GalleryItem?)
}