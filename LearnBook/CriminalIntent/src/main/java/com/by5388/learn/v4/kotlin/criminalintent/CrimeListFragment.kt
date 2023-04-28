package com.by5388.learn.v4.kotlin.criminalintent

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.by5388.learn.v4.kotlin.criminalintent.CrimeItemViewModel.CrimeCallback
import com.by5388.learn.v4.kotlin.criminalintent.databinding.FragmentCrimeListBinding

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
        }

        mCrimeListViewModel.mCrimesListLiveData.observe(viewLifecycleOwner) { crimes ->
            crimes?.let { updateUI(it) }
        }
    }

    override fun onDestroyView() {
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
}