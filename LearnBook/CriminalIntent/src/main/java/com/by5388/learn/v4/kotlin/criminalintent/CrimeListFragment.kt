package com.by5388.learn.v4.kotlin.criminalintent

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author  admin  on 2021/6/5.
 */
private const val TAG = "CrimeListFragment"
private const val TYPE_NORMAL = 0
private const val TYPE_UN_SOLVED = 1

class CrimeListFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
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

    private var mAdapter: CrimeAdapter? = null
    private var mCallback: Callback? = null

    private val mCrimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mCallback = context as Callback?
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
        mRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mCrimeListViewModel.mCrimesListLiveData
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer { crimes ->
                crimes?.let {
                    Log.d(TAG, "onViewCreated: crimes.size = ${crimes.size}")
                    updateUI(crimes)
                }
            })
    }

    override fun onDetach() {
        super.onDetach()
        mCallback = null
    }

    private fun updateUI(crimes: List<Crime>) {
        // FIXME: 2021/6/6 有一个问题没有解决：在 CrimeFragment 页面修改后，
        //  CrimeListFragment 页面无法及时刷新数据
        //mAdapter = CrimeAdapter(crimes, mDiffCallback)
        //mRecyclerView.adapter = mAdapter
        if (mAdapter == null) {
            Log.d(TAG, "updateUI: mAdapter == null")
            mAdapter = CrimeAdapter(crimes, mDiffCallback)
            mRecyclerView.adapter = mAdapter
        } else {
            Log.d(TAG, "updateUI: mAdapter != null")
            mAdapter!!.submitList(crimes)
            mRecyclerView.adapter = mAdapter
        }
    }

    private inner class CrimeAdapter(
        var crimes: List<Crime>,
        diffCallback: DiffUtil.ItemCallback<Crime>
    ) : ListAdapter<Crime, CrimeHolder>(diffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val itemLayout: Int = if (viewType == TYPE_NORMAL) {
                R.layout.list_item_crime
            } else {
                R.layout.list_item_crime_un_solved
            }
            val view = layoutInflater.inflate(itemLayout, parent, false)
            return CrimeHolder(view)
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            holder.bind(crimes[position])
        }

        override fun onBindViewHolder(
            holder: CrimeHolder,
            position: Int,
            payloads: MutableList<Any>
        ) {
            super.onBindViewHolder(holder, position, payloads)
        }

        override fun getItemCount(): Int = crimes.size

        override fun getItemViewType(position: Int): Int {
            if (crimes[position].isSolved) {
                return TYPE_NORMAL
            }
            return TYPE_UN_SOLVED
        }
    }


    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
            var imageView = itemView.findViewById(R.id.imageView) as ImageView?
            imageView?.setOnClickListener(this)
        }

        private lateinit var mCrime: Crime
        private val mTitleTextView: TextView = view.findViewById(R.id.crime_title)
        private val mDateTextView: TextView = view.findViewById(R.id.crime_date)
        private val mDateFormat =
            SimpleDateFormat(context?.getString(R.string.date_format), Locale.getDefault())
        //private var mImageView: ImageView? = view.findViewById(R.id.imageView) as ImageView?

        fun bind(crime: Crime) {
            this.mCrime = crime
            mTitleTextView.text = mCrime.title
            mDateTextView.text = mDateFormat.format(mCrime.date)
//            mImageView?.visibility = if (crime.isSolved) {
//                View.GONE
//            } else {
//                View.VISIBLE
//            }

        }

        override fun onClick(v: View?) {
            mCallback?.onCrimeSelected(mCrime.id)
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
    }


    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

    interface Callback {
        fun onCrimeSelected(crimeId: UUID);
    }
}