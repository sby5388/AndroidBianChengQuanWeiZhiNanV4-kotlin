package com.by5388.learn.v4.kotlin.photogallery.api

import com.by5388.learn.v4.kotlin.photogallery.GalleryItem
import com.google.gson.annotations.SerializedName

class PhotoResponse {
    @SerializedName("photo")
    lateinit var galleryItems: List<GalleryItem>
    var page: Int = 0
    var pages: Int = 0
    var perpage: Int = 0
    var total: Int = 0
}
