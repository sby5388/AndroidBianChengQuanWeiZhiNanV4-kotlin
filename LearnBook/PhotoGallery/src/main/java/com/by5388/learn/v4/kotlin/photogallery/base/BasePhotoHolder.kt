package com.by5388.learn.v4.kotlin.photogallery.base

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.by5388.learn.v4.kotlin.photogallery.GalleryItem

abstract class BasePhotoHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener {

    private var mGallery: GalleryItem? = null
    var mGalleryClick: GalleryClick? = null

    constructor(binding: ViewBinding) : this(binding.root)

    constructor(binding: ViewDataBinding) : this(binding.root)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        mGallery ?: return
        mGalleryClick ?: return
        mGalleryClick?.onGalleryClick(mGallery)
    }

    open fun bind(gallery: GalleryItem) {
        mGallery = gallery;
    }


}