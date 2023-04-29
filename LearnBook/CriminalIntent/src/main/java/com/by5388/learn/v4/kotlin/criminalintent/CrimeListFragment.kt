package com.by5388.learn.v4.kotlin.criminalintent

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.by5388.learn.v4.kotlin.criminalintent.CrimeItemViewModel.CrimeCallback
import com.by5388.learn.v4.kotlin.criminalintent.databinding.FragmentCrimeListBinding
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.TimeUnit

/**
 * @author  admin  on 2021/6/5.
 */

class CrimeListFragment : Fragment() {
    /**
     *  20210606 使用这个ListAdapter有个致命的问题，就是首次创建Crime,然后返回CrimeListFragment会闪退
     *  修改后的数据也没有及时更新到UI上
     *  20210627 已修复
     */
    private lateinit var mAdapter: CrimeAdapter
    private var _mBinding: FragmentCrimeListBinding? = null
    private val mBinding: FragmentCrimeListBinding
        get() = _mBinding!!

    private val mCrimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CrimeListViewModel::class.java)
    }
    private var mSnackbar: Snackbar? = null

    private val mHandler = Handler(Looper.getMainLooper(), Handler.Callback {
        val what = it.what
        if (ACTION_DELETE == what) {
            val crime = it.obj as Crime?
            crime?.let {
                mCrimeListViewModel.delete(crime)
            }
            mSnackbar?.dismiss()
            true
        }

        true
    })


    /**
     * 需要实现左右滑动item时 删除该crime
     */
    private val mTouchCallback: ItemTouchHelper.Callback = object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
        ): Int {
            //0：不支持拖拽，ItemTouchHelper.DOWN:支持向下拖拽；ItemTouchHelper.UP:支持向上拖拽；这里都不需要
            val dragFlags = 0
            //0:不支持滑动，ItemTouchHelper.START|LEFT 支持从左向右滑动时触发；ItemTouchHelper.END|RIGHT 支持从右向左滑动时触发
            val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            // TODO: 这里待确定
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            //删除之后 修改数据库，隐藏该记录，然后提示一个SnackBar来提示用户撤销，如果3秒钟后用户没有撤销，则删除该记录
            val crime = mAdapter.currentList[viewHolder.bindingAdapterPosition]
            crime.hide = true
            mCrimeListViewModel.saveCrime(crime)
            showSnackBar(crime)
        }
    }

    private val mItemTouchHelper: ItemTouchHelper by lazy {
        ItemTouchHelper(mTouchCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mAdapter = CrimeAdapter(object : CrimeCallback {
            override fun onClick(crime: Crime) {
                toDetail(crime)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _mBinding = FragmentCrimeListBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.buttonAddCrime.setOnClickListener { createCrime() }
        mBinding.crimeRecyclerView.apply {
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(), DividerItemDecoration.VERTICAL
                )
            )
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
            setHasFixedSize(true)
            mItemTouchHelper.attachToRecyclerView(this)
        }

        mCrimeListViewModel.mCrimesListLiveData.observe(viewLifecycleOwner) { crimes ->
            crimes?.let { updateUI(it) }
        }
    }

    override fun onDestroyView() {
        mBinding.crimeRecyclerView.adapter = null
        _mBinding = null
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                createCrime()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun createCrime() {
        val crime = Crime()
        mCrimeListViewModel.addCrime(crime)
        toDetail(crime)
    }

    private fun toDetail(crime: Crime) {
        val navController = findNavController()
        if (navController.currentDestination?.id != R.id.CrimeListFragment) {
            return
        }
        val build = CrimeFragmentArgs.Builder(crime.id).build()
        navController.navigate(R.id.action_list_to_detail, build.toBundle())
    }

    private fun updateUI(crimes: List<Crime>) {
        //Log.d(TAG, "updateUI: " + crimes.size)

        // 2021/6/6 有一个问题没有解决：在 CrimeFragment 页面修改后，
        //  CrimeListFragment 页面无法及时刷新数据
        // 20210627已修复
        mAdapter.submitList(crimes)
        if (crimes.isEmpty()) {
            mBinding.buttonAddCrime.visibility = View.VISIBLE
            mBinding.crimeRecyclerView.visibility = View.GONE
        } else {
            mBinding.buttonAddCrime.visibility = View.GONE
            mBinding.crimeRecyclerView.visibility = View.VISIBLE
        }

    }

    private fun showSnackBar(hideCrime: Crime) {


        val snackBar = Snackbar.make(
            mBinding.rootView, "撤销删除记录 ${hideCrime.title} ?", Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction("撤销") {
            mHandler.removeMessages(ACTION_DELETE)
            hideCrime.hide = false
            mCrimeListViewModel.saveCrime(hideCrime)

        }
        mSnackbar = snackBar
        snackBar.show()

        val message = mHandler.obtainMessage(ACTION_DELETE)
        message.obj = hideCrime
        mHandler.sendMessageDelayed(message, TimeUnit.SECONDS.toMillis(3))
    }

    companion object {
        private const val ACTION_DELETE = 100
    }


}