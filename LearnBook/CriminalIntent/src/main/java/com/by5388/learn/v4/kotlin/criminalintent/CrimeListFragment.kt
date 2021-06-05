package com.by5388.learn.v4.kotlin.criminalintent

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
import androidx.recyclerview.widget.LinearLayoutManager
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
    private var mAdapter: CrimeAdapter? = CrimeAdapter(emptyList())

    private val mCrimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
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
        mRecyclerView.adapter = mAdapter
        mCrimeListViewModel.mCrimesListLiveData
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer { crimes ->
                crimes?.let {
                    Log.d(TAG, "onViewCreated: crimes.size = ${crimes.size}")
                    updateUI(crimes)
                }
            })
    }

    private fun updateUI(crimes: List<Crime>) {
        mAdapter = CrimeAdapter(crimes)
        mRecyclerView.adapter = mAdapter
    }

    private inner class CrimeAdapter(var crimes: List<Crime>) :
        RecyclerView.Adapter<CrimeHolder>() {


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

        fun bind(crime: Crime) {
            this.mCrime = crime
            mTitleTextView.text = mCrime.title
            mDateTextView.text = mDateFormat.format(mCrime.date)
        }

        override fun onClick(v: View?) {
            val id = v?.id
            if (id == R.id.imageView) {
                Toast.makeText(
                    context,
                    "${mCrime.title} ${resources.getString(R.string.call_police)}",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            Toast.makeText(context, "${mCrime.title} pressed!", Toast.LENGTH_SHORT).show()
        }
    }


    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }
}