package com.by5388.learn.v4.kotlin.photogallery

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class PhotoPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_page)

        val fm = supportFragmentManager
        val currentFragment: Fragment? = fm.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            val uri = intent.data
            uri?.let {
                val fragment = PhotoPageFragment.newInstance(uri)
                fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit()
            }

        }
    }

    companion object {
        fun newIntent(context: Context, uri: Uri): Intent {
            return Intent(context, PhotoPageActivity::class.java).apply {
                data = uri
            }
        }
    }

}