package com.by5388.learn.v4.kotlin.criminalintent

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

/**
 * @author  admin  on 2021/6/8.
 */
private const val EXTRA_PHOTO_PATH = "photoFilePath"

class PhotoFragment : DialogFragment() {
    private lateinit var mImageView: ImageView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val rootView = inflater.inflate(R.layout.fragment_photo, null)
        mImageView = rootView.findViewById(R.id.image)


        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.view_big_photo)
            .setView(rootView)
            .setCancelable(true)
            .create()
    }

    override fun onStart() {
        super.onStart()
        val path: String? = arguments?.getString(EXTRA_PHOTO_PATH, null) as String
        Log.d(TAG, "onCreateDialog: path = $path")
        path?.let {
            //val scaledBitmap = getScaledBitmap(path, mImageView.width, mImageView.height)
            val scaledBitmap = getScaledBitmap(path, requireActivity())
            scaledBitmap.let {
                mImageView.setImageBitmap(it)
            }
        }

    }

    companion object {
        const val TAG = "PhotoFragment"
        fun newInstance(path: String): PhotoFragment {
            return PhotoFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_PHOTO_PATH, path)
                }
            }

        }
    }
}