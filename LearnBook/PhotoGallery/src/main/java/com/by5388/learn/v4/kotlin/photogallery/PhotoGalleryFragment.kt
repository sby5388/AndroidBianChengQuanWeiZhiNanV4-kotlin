package com.by5388.learn.v4.kotlin.photogallery

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView

class PhotoGalleryFragment : Fragment() {
    private lateinit var mMenuProgressBar: MenuItem
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPhotoGalleryViewModel: PhotoGalleryViewModel
    private lateinit var mThumbnailDownloader: ThumbnailDownloader<PhotoHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
                Log.d(TAG, "getGalleryItems: $galleryItems")
                hideLoadingProgress()
                mRecyclerView.adapter = PhotoAdapter(galleryItems, mThumbnailDownloader)
            })


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_photo_gallery, menu)

        val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView
        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(queryText: String): Boolean {
                    Log.d(TAG, "QueryTextSubmit: $queryText")
                    mPhotoGalleryViewModel.fetchPhotos(queryText)
                    //26.6 优化搜索栏
                    //关闭软键盘的方式1：失去焦点
                    searchView.clearFocus()
                    //关闭软键盘的方式2：调用输入法接口
                    //hideKeyboard()

                    //回收/关闭 SearchView
                    searchView.onActionViewCollapsed()
                    //展开 SearchView
                    //searchView.onActionViewExpanded()

                    resetRecyclerViewAdapter()
                    showLoadingProgress()
                    return true
                }

                override fun onQueryTextChange(queryText: String): Boolean {
                    Log.d(TAG, "QueryTextChange: $queryText")
                    return false
                }
            })

            //点击搜索按钮展开SearchView时
            setOnSearchClickListener {
                //26.4 获取上次保存的值，同时不提交查询：只显示值
                searchView.setQuery(mPhotoGalleryViewModel.mSearchTerm, false)
            }
            setOnCloseListener {
                //必须放回false(也是默认值），否则会出现无法关闭SearchView
                return@setOnCloseListener false
            }
        }

        mMenuProgressBar = menu.findItem(R.id.menu_item_progress)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_clear -> {
                mPhotoGalleryViewModel.fetchPhotos("")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    /**
     * 隐藏软键盘
     */
    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive) {
            val windowToken = requireActivity().window.decorView.windowToken
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        }
    }

    /**
     * 显示软键盘
     */
    private fun showKeyboard(view: View) {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, 0)
    }

    /**
     * 清空 RecyclerView
     */
    private fun resetRecyclerViewAdapter() {
        mRecyclerView.adapter = PhotoAdapter(mutableListOf(), mThumbnailDownloader)
    }

    /**
     * add for 26.6 状态指示器
     * 显示标题栏进度指示器
     */
    private fun showLoadingProgress() {
        mMenuProgressBar.isVisible = true

    }

    /**
     * add for 26.6 状态指示器
     * 隐藏标题栏进度指示器
     */
    private fun hideLoadingProgress() {
        mMenuProgressBar.isVisible = false
    }

}