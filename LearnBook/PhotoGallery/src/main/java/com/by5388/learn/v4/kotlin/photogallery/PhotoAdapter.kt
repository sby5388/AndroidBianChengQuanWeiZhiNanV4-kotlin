package com.by5388.learn.v4.kotlin.photogallery

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class PhotoAdapter(
    private val mGalleryItems: List<GalleryItem>,
    private val mThumbnailDownloader: ThumbnailDownloader<PhotoHolder>
) :
    RecyclerView.Adapter<PhotoHolder>() {
    private lateinit var mDefaultDrawable: Drawable

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        mDefaultDrawable = ContextCompat.getDrawable(parent.context, R.drawable.bill_up_close)!!
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_gallery, parent, false)
        return PhotoHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        val galleryItem = mGalleryItems[position]
        holder.bindGalleryItem(galleryItem)
        holder.bindDrawable(mDefaultDrawable)
        mThumbnailDownloader.queueThumbnail(holder, galleryItem.url)
    }

    override fun getItemCount(): Int {
        return mGalleryItems.size
    }
}