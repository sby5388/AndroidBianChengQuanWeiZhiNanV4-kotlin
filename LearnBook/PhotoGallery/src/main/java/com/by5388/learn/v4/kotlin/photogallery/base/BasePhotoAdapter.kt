package com.by5388.learn.v4.kotlin.photogallery.base

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.by5388.learn.v4.kotlin.photogallery.GalleryItem


abstract class BasePhotoAdapter<T : BasePhotoHolder>(val galleryClick: GalleryClick) :
    ListAdapter<GalleryItem, T>(object : DiffUtil.ItemCallback<GalleryItem>() {
        override fun areItemsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
            return TextUtils.equals(oldItem.id, newItem.id)
        }

        override fun areContentsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
            return TextUtils.equals(oldItem.toString(), newItem.toString())
        }
    })

fun View.layoutInflater(): LayoutInflater {
    return LayoutInflater.from(context)
}