package com.by5388.learn.v4.kotlin.photogallery.list

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.by5388.learn.v4.kotlin.photogallery.GalleryItem
import com.by5388.learn.v4.kotlin.photogallery.R
import com.by5388.learn.v4.kotlin.photogallery.base.BasePhotoHolder

class PhotoListHolder(itemView: View) : BasePhotoHolder(itemView),
    View.OnClickListener {

    private val mImageView: ImageView = itemView.findViewById(R.id.image_view)

    private lateinit var mGalleryItem: GalleryItem


    override fun bind(gallery: GalleryItem) {
        super.bind(gallery)
        this.mGalleryItem = gallery
    }


    //TODO 这种写法
    val bindDrawable: (Drawable) -> Unit =
        mImageView::setImageDrawable

}