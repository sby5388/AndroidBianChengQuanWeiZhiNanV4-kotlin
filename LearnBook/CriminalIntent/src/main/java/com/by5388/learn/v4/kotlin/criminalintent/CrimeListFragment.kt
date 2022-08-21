package com.by5388.learn.v4.kotlin.criminalintent

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author  admin  on 2021/6/5.
 */
private const val TAG = "CrimeListFragment"
private const val TYPE_NORMAL = 0
private const val TYPE_UN_SOLVED = 1
private const val TYPE_EMPTY = 2

class CrimeListFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAddButton: Button
    private val mDiffCallback: DiffUtil.ItemCallback<Crime> =
        object : DiffUtil.ItemCallback<Crime>() {
            override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
                return oldItem.id == newItem.id &&
                        oldItem.date == newItem.date &&
                        oldItem.title == newItem.title &&
                        oldItem.isSolved == newItem.isSolved
            }
        }

    /**
     *  20210606 使用这个ListAdapter有个致命的问题，就是首次创建Crime,然后返回CrimeListFragment会闪退
     *  修改后的数据也没有及时更新到UI上
     *  20210627 已修复
     */
    private var mAdapter: CrimeAdapter? = null

    private val mCrimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CrimeListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_crime_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAddButton = view.findViewById(R.id.button_add_crime)
        mAddButton.setOnClickListener {
            createCrime()
        }
        mRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        mRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mAdapter = CrimeAdapter(mDiffCallback)
        mRecyclerView.adapter = mAdapter
        mCrimeListViewModel.mCrimesListLiveData
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer { crimes ->
                crimes?.let {
                    Log.d(TAG, "onViewCreated: crimes.size = ${crimes.size}")
                    //updateUI2(crimes)
                    updateUI(crimes)
                }
            })
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
        NavHostFragment.findNavController(this@CrimeListFragment)
            .navigate(R.id.action_list_to_detail, CrimeFragment.newBundle(crime.id))
    }

    private fun updateUI(crimes: List<Crime>) {
        //Log.d(TAG, "updateUI: " + crimes.size)

        // 2021/6/6 有一个问题没有解决：在 CrimeFragment 页面修改后，
        //  CrimeListFragment 页面无法及时刷新数据
        // 20210627已修复
        mAdapter!!.submitList(crimes)
        if (crimes.isEmpty()) {
            mAddButton.visibility = View.VISIBLE
            mRecyclerView.visibility = View.GONE
        } else {
            mAddButton.visibility = View.GONE
            mRecyclerView.visibility = View.VISIBLE
        }

    }

    private inner class CrimeAdapter(
        diffCallback: DiffUtil.ItemCallback<Crime>
    ) : ListAdapter<Crime, CrimeHolder>(diffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view)
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            holder.bind(getItem(position))
        }
    }

    private open inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
            var imageView = itemView.findViewById(R.id.imageView) as ImageView?
            imageView?.setOnClickListener(this)
        }

        private lateinit var mCrime: Crime
        private val mRootView: View = view.findViewById(R.id.linearLayout2)
        private val mImageView: ImageView = view.findViewById(R.id.imageView)
        private val mTitleTextView: TextView = view.findViewById(R.id.crime_title)
        private val mDateTextView: TextView = view.findViewById(R.id.crime_date)
        private val mDateFormat =
            SimpleDateFormat(context?.getString(R.string.date_format), Locale.getDefault())
        //private var mImageView: ImageView? = view.findViewById(R.id.imageView) as ImageView?


        open fun bind(crime: Crime) {
            this.mCrime = crime
            mTitleTextView.text = mCrime.title
            mDateTextView.text = mDateFormat.format(mCrime.date)
            mImageView.visibility = if (crime.isSolved) {
                View.GONE
            } else {
                View.VISIBLE
            }
            mRootView.contentDescription = "${mCrime.title}," +
                    getString(getSolved(mCrime)) +
                    " ,${mDateTextView.text} "

        }

        override fun onClick(v: View?) {
            toDetail(mCrime)
            val id = v?.id
            if (id == R.id.imageView) {
                Toast.makeText(
                    context,
                    "${mCrime.title} ${resources.getString(R.string.call_police)}",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            //Toast.makeText(context, "${mCrime.title} pressed!", Toast.LENGTH_SHORT).show()
        }

        private fun getSolved(crime: Crime): Int {
            return if (crime.isSolved) {
                R.string.crime_report_solved
            } else {
                R.string.crime_report_unsolved
            }
        }

    }
}