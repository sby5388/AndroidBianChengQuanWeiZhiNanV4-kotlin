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
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import java.util.concurrent.TimeUnit

private const val POLL_WORK = "POLL_WORK"

class PhotoGalleryFragment : VisibleFragment() {
    private lateinit var mMenuProgressBar: MenuItem
    private lateinit var mMenuItemTogglePolling: MenuItem
    private lateinit var mMenuItemToggleChromeTab: MenuItem
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPhotoGalleryViewModel: PhotoGalleryViewModel
    private lateinit var mThumbnailDownloader: ThumbnailDownloader<PhotoHolder>

    private val mCallback: PhotoHolder.ItemCallback = object : PhotoHolder.ItemCallback {
        override fun onClick(item: GalleryItem) {
            if (mMenuItemToggleChromeTab.isChecked) {
                CustomTabsIntent.Builder()
                    .setDefaultColorSchemeParams(
                        CustomTabColorSchemeParams.Builder()
                            .setToolbarColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.colorPrimary
                                )
                            )
                            .setSecondaryToolbarColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.colorPrimaryVariant
                                )
                            )
                            .build()
                    )
                    .setShowTitle(true)
                    .build()
                    .launchUrl(requireContext(), item.mPhotoPageUri)
            } else {
                val intent = PhotoPageActivity.newIntent(requireContext(), item.mPhotoPageUri)
                startActivity(intent)
            }
        }
    }

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
                Log.d(TAG, "getGalleryItems: ${galleryItems.size}")
                hideLoadingProgress()
                mRecyclerView.adapter = PhotoAdapter(galleryItems, mCallback, mThumbnailDownloader)
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
        mMenuItemTogglePolling = menu.findItem(R.id.menu_item_toggle_polling)
        mMenuItemToggleChromeTab = menu.findItem(R.id.menu_item_toggle_chrome_custom_tab)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_clear -> {
                mPhotoGalleryViewModel.fetchPhotos("")
                true
            }
            R.id.menu_item_toggle_polling -> {
                togglePolling()
                requireActivity().invalidateOptionsMenu()
                true
            }
            R.id.menu_item_toggle_chrome_custom_tab -> {
                toggleUseChromeCustomTab()
                requireActivity().invalidateOptionsMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val isPolling = QueryPreferences.isPolling(requireContext())
        val toggleItemTitle = if (isPolling) {
            R.string.stop_polling
        } else {
            R.string.start_polling
        }
        mMenuItemTogglePolling.isChecked = isPolling
        mMenuItemTogglePolling.title = getString(toggleItemTitle)
        mMenuItemToggleChromeTab.isChecked = QueryPreferences.isUseChromeCustomTab(requireContext())
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
        mRecyclerView.adapter = PhotoAdapter(mutableListOf(), mCallback, mThumbnailDownloader)
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

    private fun toggleUseChromeCustomTab() {
        val useChromeCustomTab = QueryPreferences.isUseChromeCustomTab(requireContext())
        QueryPreferences.setUseChromeCustomTab(requireContext(), !useChromeCustomTab)
    }

    private fun togglePolling() {
        val isPolling = QueryPreferences.isPolling(requireContext())
        if (isPolling) {
            Log.d(TAG, "togglePolling: stop polling")
            WorkManager.getInstance().cancelAllWorkByTag(POLL_WORK)
        } else {
            Log.d(TAG, "togglePolling: start polling")
            //设置work启动的条件
            //NetworkType.NOT_REQUIRED:不需要网络
            //NetworkType.CONNECTED:任何已连接的网络
            //NetworkType.UNMETERED:不限制流量的网络，包括WIFI和以太网
            //NetworkType.NOT_ROAMING:非漫游的网络
            //NetworkType.METERED:按流量计费的网络，移动网络
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            //OneTimeWorkRequest:执行一次性任务
            //PeriodicWorkRequest:定期执行任务
            val periodRequest = PeriodicWorkRequest
                //每十五分钟执行一次:
                // todo 15分钟 这也是最短的时间间隔，防止系统过于频繁地执行同一任务，从而节约资源，如电池
                .Builder(PollWorker::class.java, 15, TimeUnit.MINUTES)
                //增加网络类型限制
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance()
                // TODO: 2021/7/17 各个参数的含义
                //  名称；当前的服务策略；网络服务请求(要做的事情)
                //  名称参数：唯一性，标识网络请求，停止服务时引用
                //  当前服务策略告诉WorkManager该如何对待已计划安排好的具名工作任务。
                //  这里使用的是KEEP策略，意思是保留当前服务，不接受安排新的后台服务。
                //  当前服务策略的另一个选择是REPLACE，顾名思义，就是使用新的后台服务替换当前服务。
                .enqueueUniquePeriodicWork(
                    POLL_WORK,
                    ExistingPeriodicWorkPolicy.KEEP,
                    periodRequest
                )
        }
        //set newValue
        QueryPreferences.setPolling(requireContext(), !isPolling)
    }

}