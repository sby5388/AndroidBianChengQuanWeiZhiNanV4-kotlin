package com.by5388.learn.v4.kotlin.criminalintent

import androidx.recyclerview.widget.RecyclerView
import com.by5388.learn.v4.kotlin.criminalintent.databinding.ListItemCrimeBinding

class CrimeHolder(
    private val mBinding: ListItemCrimeBinding,
    callback: CrimeItemViewModel.CrimeCallback
) :
    RecyclerView.ViewHolder(mBinding.root) {

    private val crimeItemViewModel = CrimeItemViewModel(itemView.context)

    init {
        mBinding.viewModel = crimeItemViewModel
        crimeItemViewModel.callback = callback
    }

    fun bind(crime: Crime) {
        crimeItemViewModel.bind(crime)
        mBinding.executePendingBindings()
    }


}