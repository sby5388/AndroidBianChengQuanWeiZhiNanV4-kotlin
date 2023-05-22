package com.by5388.learn.v4.kotlin.photogallery.picasso

import com.by5388.learn.v4.kotlin.photogallery.base.BasePhotoHolder
import com.by5388.learn.v4.kotlin.photogallery.GalleryItem
import com.by5388.learn.v4.kotlin.photogallery.R
import com.by5388.learn.v4.kotlin.photogallery.databinding.GlideListItemGalleryBinding
import com.squareup.picasso.Picasso

class PicassoPhotoHolder(private val binding: GlideListItemGalleryBinding) :
    BasePhotoHolder(binding) {

    override fun bind(gallery: GalleryItem) {
        super.bind(gallery)
        Picasso.get()
            .load(gallery.url)
            .placeholder(R.drawable.bill_up_close)
            .into(binding.imageView)
    }

}