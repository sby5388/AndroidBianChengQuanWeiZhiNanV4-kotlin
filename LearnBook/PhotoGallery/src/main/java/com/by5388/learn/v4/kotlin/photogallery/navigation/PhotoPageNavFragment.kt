package com.by5388.learn.v4.kotlin.photogallery.navigation

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.by5388.learn.v4.kotlin.photogallery.databinding.FragmentPhotoPageBinding

private const val ARG_URI = "photo_page_url"

class PhotoPageNavFragment : BaseVisibleFragment() {
    private var _binding: FragmentPhotoPageBinding? = null
    private val mBinding: FragmentPhotoPageBinding
        get() = _binding!!


    private lateinit var mUri: Uri
    private lateinit var mWebView: WebView

    val canGoBack: Boolean
        get() = if (mWebView.canGoBack()) {
            mWebView.goBack()
            true
        } else {
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUri = if (arguments != null) {
            PhotoPageNavFragmentArgs.fromBundle(arguments!!).mUri
        } else {
            Uri.EMPTY
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoPageBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.webView.apply {
            settings.javaScriptEnabled = true
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    if (_binding == null) {
                        return
                    }
                    if (newProgress == 100) {
                        mBinding.progressHorizontal.visibility = View.GONE
                    } else {
                        mBinding.progressHorizontal.progress = newProgress
                        mBinding.progressHorizontal.visibility = View.VISIBLE
                    }
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    if (_binding == null) {
                        return
                    }
                    val appCompatActivity = requireActivity() as AppCompatActivity
                    appCompatActivity.supportActionBar?.subtitle = title
                }


            }
            loadUrl(mUri.toString())
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(ARG_URI, mUri)
    }
}