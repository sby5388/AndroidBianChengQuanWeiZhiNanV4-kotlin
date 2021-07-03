package com.by5388.learn.v4.kotlin.photogallery

import com.google.gson.annotations.SerializedName

data class GalleryItem(
    var title: String = "",
    var id: String = "",
    @SerializedName("url_s")
    var url: String = ""
)
