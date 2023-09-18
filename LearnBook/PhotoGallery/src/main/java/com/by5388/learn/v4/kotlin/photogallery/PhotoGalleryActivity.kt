package com.by5388.learn.v4.kotlin.photogallery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

@Deprecated("see [com.by5388.learn.v4.kotlin.photogallery.navigation.PhotoGalleryNavigationActivity]")
class PhotoGalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_gallery)
        val empty = savedInstanceState == null
        if (empty) {
            supportFragmentManager
                .beginTransaction()
                .add(
                    R.id.fragment_container,
                    PhotoGalleryFragment.newInstance(),
                    PhotoGalleryFragment.TAG
                )
                .commit()
        }
    }

    fun test() {
        println("test---")
    }
}