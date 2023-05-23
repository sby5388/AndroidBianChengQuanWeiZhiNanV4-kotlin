package com.by5388.learn.v4.kotlin.photogallery.fresco

import android.view.ViewGroup
import com.by5388.learn.v4.kotlin.photogallery.base.BasePhotoAdapter
import com.by5388.learn.v4.kotlin.photogallery.base.GalleryClick
import com.by5388.learn.v4.kotlin.photogallery.base.layoutInflater
import com.by5388.learn.v4.kotlin.photogallery.databinding.FrescoListItemGalleryBinding

class FrescoAdapter(click: GalleryClick) : BasePhotoAdapter<FrescoHolder>(click) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FrescoHolder {
        val holder = FrescoHolder(
            FrescoListItemGalleryBinding.inflate(
                parent.layoutInflater(),
                parent,
                false
            )
        )
        holder.mGalleryClick = galleryClick
        return holder
    }

    override fun onBindViewHolder(holder: FrescoHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}