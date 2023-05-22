package com.by5388.learn.v4.kotlin.photogallery.glide

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.by5388.learn.v4.kotlin.photogallery.base.BasePhotoHolder
import com.by5388.learn.v4.kotlin.photogallery.GalleryItem
import com.by5388.learn.v4.kotlin.photogallery.R
import com.by5388.learn.v4.kotlin.photogallery.databinding.GlideListItemGalleryBinding

class GlidePhotoHolder(private val binding: GlideListItemGalleryBinding) :
    BasePhotoHolder(binding) {


    override fun bind(gallery: GalleryItem) {
        super.bind(gallery)
        Glide.with(binding.imageView)
            .asDrawable()
            .load(gallery.url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.bill_up_close)
            .into(binding.imageView)
    }

}