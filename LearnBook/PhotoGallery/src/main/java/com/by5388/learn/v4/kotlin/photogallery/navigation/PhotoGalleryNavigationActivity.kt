package com.by5388.learn.v4.kotlin.photogallery.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.by5388.learn.v4.kotlin.photogallery.R
import com.by5388.learn.v4.kotlin.photogallery.databinding.ActivityPhotoGalleryNavigationBinding

class PhotoGalleryNavigationActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPhotoGalleryNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_gallery)
        appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_gallery)
        return (NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp())
    }

}