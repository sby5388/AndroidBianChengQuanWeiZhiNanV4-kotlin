package com.by5388.learn.v4.kotlin.photogallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView

class PhotoGalleryFragment : Fragment() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPhotoGalleryViewModel: PhotoGalleryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPhotoGalleryViewModel = ViewModelProviders.of(this).get(PhotoGalleryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photo_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = view.findViewById(R.id.photo_recycler_view)

        mPhotoGalleryViewModel.mGalleryItemLiveData
            .observe(viewLifecycleOwner, Observer { galleryItems ->
                Log.d(TAG, "onViewCreated: $galleryItems")
                mRecyclerView.adapter = PhotoAdapter(galleryItems)
            })


    }

    companion object {
        const val TAG = "PhotoGalleryFragment";
        fun newInstance(): PhotoGalleryFragment {
            return PhotoGalleryFragment();
        }

        private fun newInstance2() = PhotoGalleryFragment()
    }

    fun temp() {
        Log.d(TAG, "temp: ")
    }

}