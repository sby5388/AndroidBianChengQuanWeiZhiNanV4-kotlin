package com.by5388.learn.v4.kotlin.photogallery

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class GalleryItem(
    var title: String = "",
    var id: String = "",
    @SerializedName("url_s")
    var url: String = "",
    @SerializedName("owner")
    var owner: String = ""
) {
    /**
     * 大图的uri,所在的网页链接，并不是图片的下载链接
     * https://www.flickr.com/photos/owner/id
     */
    val mPhotoPageUri: Uri
        get() {
            return Uri.parse("https://www.flickr.com/photos/")
                .buildUpon()
                .appendPath(owner)
                .appendPath(id)
                .build()
        }
}
