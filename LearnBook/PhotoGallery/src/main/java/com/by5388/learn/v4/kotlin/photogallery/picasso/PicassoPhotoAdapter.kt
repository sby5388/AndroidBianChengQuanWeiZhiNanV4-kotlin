package com.by5388.learn.v4.kotlin.photogallery.picasso

import android.view.ViewGroup
import com.by5388.learn.v4.kotlin.photogallery.base.BasePhotoAdapter
import com.by5388.learn.v4.kotlin.photogallery.base.GalleryClick
import com.by5388.learn.v4.kotlin.photogallery.databinding.GlideListItemGalleryBinding
import com.by5388.learn.v4.kotlin.photogallery.base.layoutInflater

class PicassoPhotoAdapter(galleryClick: GalleryClick) :
    BasePhotoAdapter<PicassoPhotoHolder>(galleryClick) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicassoPhotoHolder {
        val binding = GlideListItemGalleryBinding.inflate(parent.layoutInflater(), parent, false)
        val holder = PicassoPhotoHolder(binding)
        holder.mGalleryClick = galleryClick
        return holder
    }

    override fun onBindViewHolder(holder: PicassoPhotoHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}