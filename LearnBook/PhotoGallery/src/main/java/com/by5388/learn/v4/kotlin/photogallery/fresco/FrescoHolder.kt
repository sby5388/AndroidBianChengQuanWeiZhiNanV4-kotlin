package com.by5388.learn.v4.kotlin.photogallery.fresco

import com.by5388.learn.v4.kotlin.photogallery.GalleryItem
import com.by5388.learn.v4.kotlin.photogallery.base.BasePhotoHolder
import com.by5388.learn.v4.kotlin.photogallery.databinding.FrescoListItemGalleryBinding


class FrescoHolder(private val mBinding: FrescoListItemGalleryBinding) :
    BasePhotoHolder(mBinding) {

    override fun bind(gallery: GalleryItem) {
        super.bind(gallery)
        mBinding.myImageView.setImageURI(gallery.url)
    }
}