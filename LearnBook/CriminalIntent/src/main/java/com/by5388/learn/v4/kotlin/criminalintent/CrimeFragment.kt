package com.by5388.learn.v4.kotlin.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author  admin  on 2021/6/5.
 */
private const val ARG_CRIME_ID = "crime_id"

class CrimeFragment : Fragment() {

    private lateinit var mCrime: Crime
    private lateinit var mTitleField: EditText
    private lateinit var mDateButton: Button
    private lateinit var mSolvedCheckBox: CheckBox
    private lateinit var mDateFormat: DateFormat


    private val mCrimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCrime = Crime()
        val crimeID: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        mCrimeDetailViewModel.loadCrime(crimeID)
        mDateFormat = SimpleDateFormat(
            context?.getString(
                R.string.date_format
            ), Locale.getDefault()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_crime, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTitleField = view.findViewById<EditText>(R.id.crime_title)
        mDateButton = view.findViewById(R.id.crime_date)
        mSolvedCheckBox = view.findViewById(R.id.crime_solved)

        mDateButton.apply {
            text = mDateFormat.format(mCrime.date)
            isEnabled = false
        }

        mCrimeDetailViewModel.mCrimeLiveData
            .observe(viewLifecycleOwner,
                Observer { crime ->
                    crime?.let {
                        this.mCrime = crime
                        updateUI()
                    }
                })

    }

    override fun onStart() {
        super.onStart()
        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mCrime.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        mTitleField.addTextChangedListener(titleWatcher)

        mSolvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                mCrime.isSolved = isChecked
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mCrimeDetailViewModel.saveCrime(mCrime)
    }

    private fun updateUI() {
        mTitleField.setText(mCrime.title)
        mDateButton.text = mDateFormat.format(mCrime.date)
        mSolvedCheckBox.apply {
            isChecked = mCrime.isSolved
            // TODO: 2021/6/6 跳过此次动画，不影响手动勾选的效果
            jumpDrawablesToCurrentState()
        }

    }


    companion object {
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }

        }
    }
}