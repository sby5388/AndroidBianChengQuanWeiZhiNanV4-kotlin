package com.by5388.learn.v4.kotlin.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.by5388.learn.v4.kotlin.criminalintent.databinding.DialogPickerDateBinding
import java.util.*

class DatePickerDialogFragment : DialogFragment() {
    private var _mBinding: DialogPickerDateBinding? = null
    private val mBinding: DialogPickerDateBinding
        get() = _mBinding!!

    private val mDateArgs: DatePickerDialogFragmentArgs by lazy {
        DatePickerDialogFragmentArgs.fromBundle(arguments ?: throw NullPointerException())
    }

    private val model: CrimeDetailViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CrimeDetailViewModel::class.java)
    }
    private lateinit var mCalendar: Calendar

    private val mDateChangedListener: DatePicker.OnDateChangedListener by lazy {
        DatePicker.OnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            //如果不加上lazy 下面这行代码会提示mDateChangedListener没有被初始化
            mBinding.datePicker.init(year, monthOfYear, dayOfMonth, mDateChangedListener)
            mCalendar.set(year, monthOfYear, dayOfMonth)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _mBinding = DialogPickerDateBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCalendar = Calendar.getInstance()
        mCalendar.time = mDateArgs.date

        val year = mCalendar.get(Calendar.YEAR)
        val monthOfYear = mCalendar.get(Calendar.MONTH)
        val dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH)
        mBinding.datePicker.init(
            year, monthOfYear, dayOfMonth, mDateChangedListener
        )

        mBinding.buttonBar.buttonOk.setOnClickListener {
            model.updateCrime(mDateArgs.crimeID, mCalendar.time)
            dismiss()
        }

        mBinding.buttonBar.buttonCancel.setOnClickListener {
            dismiss()
        }

    }

    override fun onDestroyView() {
        mBinding.datePicker.init(
            mCalendar.get(Calendar.YEAR),
            mCalendar.get(Calendar.MONTH),
            mCalendar.get(Calendar.DAY_OF_MONTH),
            null
        )
        _mBinding = null
        super.onDestroyView()
    }
}