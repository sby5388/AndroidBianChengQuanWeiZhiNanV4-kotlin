package com.by5388.learn.v4.kotlin.photogallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PhotoAdapter(private val mGalleryItems: List<GalleryItem>) :
    RecyclerView.Adapter<PhotoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_photo, parent, false)
        return PhotoHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        holder.bind(mGalleryItems[position])

    }

    override fun getItemCount(): Int {
        return mGalleryItems.size
    }
}