package com.by5388.learn.v4.kotlin.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import java.util.*

/**
 * @author  admin  on 2021/6/6.
 */
private const val EXTRA_DATE = "com.by5388.learn.v4.kotlin.criminalintent.extra_date"
private const val EXTRA_UUID = "com.by5388.learn.v4.kotlin.criminalintent.extra_id"

class DatePickerFragment : DialogFragment() {
    private val model: CrimeDetailViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CrimeDetailViewModel::class.java)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = arguments?.getSerializable(EXTRA_DATE) as Date ?: Date()
        val uuid = arguments?.getSerializable(EXTRA_UUID) as UUID ?: null

        val calendar = Calendar.getInstance()
        calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dateSetListener: DatePickerDialog.OnDateSetListener =
            DatePickerDialog.OnDateSetListener { _, _year, _month, _dayOfMonth ->
                uuid?.let {
                    val resultDate = GregorianCalendar(_year, _month, _dayOfMonth).time
                    model.updateCrime(it, resultDate)
                }
            }

        return DatePickerDialog(
            requireContext(),
            dateSetListener,
            year, month, day
        )
    }

    companion object {
        fun newInstance(date: Date, id: UUID): DatePickerFragment {
            val args = Bundle().apply {
                putSerializable(EXTRA_DATE, date)
                putSerializable(EXTRA_UUID, id)
            }
            return DatePickerFragment().apply {
                arguments = args
            }
        }
    }


}