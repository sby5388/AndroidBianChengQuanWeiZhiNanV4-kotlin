package com.by5388.learn.v4.kotlin.photogallery

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PhotoHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener {

    init {
        itemView.setOnClickListener(this)
    }

    private val mImageView: ImageView = itemView.findViewById(R.id.image_view)


    private lateinit var mGalleryItem: GalleryItem

    override fun onClick(v: View) {
        val intent = PhotoPageActivity.newIntent(v.context, mGalleryItem.mPhotoPageUri)
        v.context.startActivity(intent)
    }


    fun bindGalleryItem(item: GalleryItem) {
        this.mGalleryItem = item
    }

    //TODO 这种写法
    val bindDrawable: (Drawable) -> Unit =
        mImageView::setImageDrawable

}