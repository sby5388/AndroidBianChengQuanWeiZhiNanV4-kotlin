package com.by5388.learn.v4.kotlin.photogallery

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PhotoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val mImageView: ImageView = itemView.findViewById(R.id.image_view)

    //TODO 这种写法
    val bindDrawable: (Drawable) -> Unit =
        mImageView::setImageDrawable

}