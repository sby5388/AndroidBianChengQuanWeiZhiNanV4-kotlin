package com.by5388.learn.v4.kotlin.photogallery.glide

import android.view.ViewGroup
import com.by5388.learn.v4.kotlin.photogallery.base.BasePhotoAdapter
import com.by5388.learn.v4.kotlin.photogallery.base.GalleryClick
import com.by5388.learn.v4.kotlin.photogallery.databinding.GlideListItemGalleryBinding
import com.by5388.learn.v4.kotlin.photogallery.base.layoutInflater

class GlidePhotoPhotoAdapter(galleryClick: GalleryClick) :
    BasePhotoAdapter<GlidePhotoHolder>(galleryClick) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GlidePhotoHolder {
        val binding = GlideListItemGalleryBinding.inflate(parent.layoutInflater(), parent, false)
        val holder = GlidePhotoHolder(binding)
        holder.mGalleryClick = galleryClick
        return holder
    }

    override fun onBindViewHolder(holder: GlidePhotoHolder, position: Int) {
        //一个是用于获取元素位于当前绑定Adapter的位置，
        //holder.bindingAdapterPosition
        // 一个是用于获取元素位于Adapter中的绝对位置（一般常用的就是这个）。
        //holder.absoluteAdapterPosition
        val item = getItem(position)
        holder.bind(item)
    }

}
