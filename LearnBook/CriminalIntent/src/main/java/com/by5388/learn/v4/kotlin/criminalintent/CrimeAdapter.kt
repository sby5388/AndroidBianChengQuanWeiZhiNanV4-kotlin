package com.by5388.learn.v4.kotlin.criminalintent

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.by5388.learn.v4.kotlin.criminalintent.databinding.ListItemCrimeBinding


private const val TAG = "CrimeListFragment"
private const val TYPE_NORMAL = 0
private const val TYPE_UN_SOLVED = 1
private const val TYPE_EMPTY = 2

class CrimeAdapter(private val callback: CrimeItemViewModel.CrimeCallback) :
    ListAdapter<Crime, CrimeHolder>(object : DiffUtil.ItemCallback<Crime>() {
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.date == newItem.date &&
                    oldItem.title == newItem.title &&
                    oldItem.isSolved == newItem.isSolved
        }
    }) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
        val binding = ListItemCrimeBinding.inflate(parent.inflater(), parent, false)
        return CrimeHolder(binding, callback)
    }

    override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
        val crime = getItem(position)
        holder.bind(crime)
    }
}