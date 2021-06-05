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

/**
 * @author  admin  on 2021/6/5.
 */
class CrimeFragment : Fragment() {

    private lateinit var mCrime: Crime
    private lateinit var mTitleField: EditText
    private lateinit var mDateButton: Button
    private lateinit var mSolvedCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCrime = Crime()
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
            text = mCrime.date.toString()
            isEnabled = false
        }
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
}