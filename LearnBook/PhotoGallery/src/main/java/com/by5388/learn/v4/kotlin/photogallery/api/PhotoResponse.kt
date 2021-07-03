package com.by5388.learn.v4.kotlin.photogallery.api

import com.by5388.learn.v4.kotlin.photogallery.GalleryItem
import com.google.gson.annotations.SerializedName

class PhotoResponse {
    @SerializedName("photo")
    lateinit var galleryItems: List<GalleryItem>
}
