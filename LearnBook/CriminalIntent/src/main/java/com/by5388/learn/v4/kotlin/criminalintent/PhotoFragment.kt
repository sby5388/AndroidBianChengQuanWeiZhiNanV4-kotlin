package com.by5388.learn.v4.kotlin.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.by5388.learn.v4.kotlin.criminalintent.databinding.FragmentPhotoBinding

/**
 * @author  admin  on 2021/6/8.
 */

class PhotoFragment : DialogFragment() {
    private var _mBinding: FragmentPhotoBinding? = null
    private val mBinding: FragmentPhotoBinding
        get() = _mBinding!!

    private lateinit var mPhotoArgs: PhotoFragmentArgs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPhotoArgs = PhotoFragmentArgs.fromBundle(it)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _mBinding = FragmentPhotoBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPhotoArgs.filePath.let {
            val scaledBitmap = getScaledBitmap(it, requireActivity())
            scaledBitmap.let { bitmap ->
                mBinding.image.setImageBitmap(bitmap)
            }
        }
    }

    override fun onDestroyView() {
        _mBinding = null
        super.onDestroyView()
    }
}