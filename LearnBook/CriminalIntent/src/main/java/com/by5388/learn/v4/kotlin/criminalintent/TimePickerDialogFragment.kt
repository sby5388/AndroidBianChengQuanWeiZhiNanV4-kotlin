package com.by5388.learn.v4.kotlin.criminalintent

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.by5388.learn.v4.kotlin.criminalintent.databinding.DialogPickerTimeBinding
import java.util.*

/**
 * 选择日期的对话框
 */
class TimePickerDialogFragment : DialogFragment() {

    private var _binding: DialogPickerTimeBinding? = null
    private val binding: DialogPickerTimeBinding
        get() = _binding!!

    private val mTimeArgs: TimePickerDialogFragmentArgs by lazy {
        TimePickerDialogFragmentArgs.fromBundle(arguments ?: throw NullPointerException())
    }


    private val model: CrimeDetailViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CrimeDetailViewModel::class.java)
    }

    private val timeChangeListener: TimePicker.OnTimeChangedListener =
        TimePicker.OnTimeChangedListener { _, hourOfDay, minute ->
            mCalendar.set(Calendar.HOUR, hourOfDay)
            mCalendar.set(Calendar.MINUTE, minute)
        }

    private lateinit var mCalendar: Calendar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPickerTimeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCalendar = Calendar.getInstance().apply {
            time = mTimeArgs.date
        }

        binding.timePicker.apply {
            setIs24HourView(false)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                currentHour = mCalendar.get(Calendar.HOUR)
                currentMinute = mCalendar.get(Calendar.MINUTE)
            } else {
                hour = mCalendar.get(Calendar.HOUR)
                minute = mCalendar.get(Calendar.MINUTE)
            }
            setOnTimeChangedListener(timeChangeListener)
        }

        binding.buttonBar.buttonCancel.setOnClickListener {
            dismiss()
        }
        binding.buttonBar.buttonOk.setOnClickListener {
            model.updateCrime(mTimeArgs.crimeID, mCalendar.time)
            dismiss()
        }
    }

    override fun onDestroyView() {
        binding.timePicker.setOnTimeChangedListener(null)
        _binding = null
        super.onDestroyView()
    }
}