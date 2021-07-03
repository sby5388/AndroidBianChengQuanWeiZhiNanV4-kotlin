package com.by5388.learn.v4.kotlin.photogallery

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PhotoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val mTextView: TextView = itemView.findViewById(R.id.textView_title)
    fun bind(galleryItem: GalleryItem) {
        mTextView.text = galleryItem.title
    }
}