package com.by5388.learn.v4.kotlin.photogallery.list

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.by5388.learn.v4.kotlin.photogallery.R
import com.by5388.learn.v4.kotlin.photogallery.ThumbnailDownloader
import com.by5388.learn.v4.kotlin.photogallery.base.BasePhotoAdapter
import com.by5388.learn.v4.kotlin.photogallery.base.GalleryClick

class PhotoListAdapter(
    galleryClick: GalleryClick,
    private val mThumbnailDownloader: ThumbnailDownloader<PhotoListHolder>
) : BasePhotoAdapter<PhotoListHolder>(galleryClick) {
    private lateinit var mDefaultDrawable: Drawable

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoListHolder {
        mDefaultDrawable = ContextCompat.getDrawable(parent.context, R.drawable.bill_up_close)!!
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_gallery, parent, false)
        val holder = PhotoListHolder(view)
        holder.mGalleryClick = galleryClick
        return holder
    }

    override fun onBindViewHolder(holder: PhotoListHolder, position: Int) {
        val galleryItem = getItem(position)
        holder.bind(galleryItem)
        holder.bindDrawable(mDefaultDrawable)
        mThumbnailDownloader.queueThumbnail(holder, galleryItem.url)
    }
}