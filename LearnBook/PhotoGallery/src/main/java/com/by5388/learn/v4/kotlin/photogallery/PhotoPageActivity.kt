package com.by5388.learn.v4.kotlin.photogallery

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

@Deprecated(
    message = "使用Navigation", ReplaceWith(
        "PhotoGalleryNavigationActivity",
         "com.by5388.learn.v4.kotlin.photogallery.navigation.PhotoGalleryNavigationActivity"
    )
)
class PhotoPageActivity : AppCompatActivity() {

    private lateinit var mFragment: PhotoPageFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_page)

        val fm = supportFragmentManager
        val currentFragment: Fragment? = fm.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            val uri = intent.data
            uri?.let {
                mFragment = PhotoPageFragment.newInstance(uri)
                fm.beginTransaction()
                    .add(R.id.fragment_container, mFragment)
                    .commit()
            }

        } else {
            mFragment = currentFragment as PhotoPageFragment
        }
    }

    override fun onBackPressed() {
        if (mFragment.canGoBack) {
            return
        }
        super.onBackPressed()
    }

    companion object {
        fun newIntent(context: Context, uri: Uri): Intent {
            return Intent(context, PhotoPageActivity::class.java).apply {
                data = uri
            }
        }
    }

}