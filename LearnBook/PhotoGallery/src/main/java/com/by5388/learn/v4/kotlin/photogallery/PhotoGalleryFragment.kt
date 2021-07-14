package com.by5388.learn.v4.kotlin.photogallery

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
    private lateinit var mThumbnailDownloader: ThumbnailDownloader<PhotoHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        mPhotoGalleryViewModel = ViewModelProviders.of(this).get(PhotoGalleryViewModel::class.java)
        val responseHandler: Handler = Handler(Looper.getMainLooper())

        mThumbnailDownloader = ThumbnailDownloader(responseHandler) { holder, bitmap ->
            val drawable = BitmapDrawable(resources, bitmap)
            holder.bindDrawable(drawable)
        }
        //todo 添加观察者
        lifecycle.addObserver(mThumbnailDownloader.fragmentLifecycleObserver)

        //todo add for 25.10 lifecycleOwner
        //viewLifecycleOwnerLiveData.observe(this, {
        //    it.lifecycle.addObserver(mThumbnailDownloader.fragmentLifecycleObserver)
        //})
        // TODO: 2021/7/13 如果这里调用的使用   viewLifecycleOwnerLiveData.observe(viewLifecycleOwner,)
        //  则会闪退，提示 只能在 "Can't access the Fragment View's LifecycleOwner when getView() is null i.e.,
        //  before onCreateView() or after onDestroyView()"

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
                mRecyclerView.adapter = PhotoAdapter(galleryItems, mThumbnailDownloader)
            })


    }

    override fun onDestroy() {
        super.onDestroy()
        // 移除观察者:其实会自动移除的，
        lifecycle.removeObserver(mThumbnailDownloader.fragmentLifecycleObserver)
    }

    companion object {
        const val TAG = "PhotoGalleryFragment";
        fun newInstance(): PhotoGalleryFragment {
            return PhotoGalleryFragment();
        }

        private fun newInstance2() = PhotoGalleryFragment()
    }


}