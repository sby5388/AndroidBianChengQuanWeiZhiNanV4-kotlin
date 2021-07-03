package com.by5388.learn.v4.kotlin.criminalintent

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author  admin  on 2021/6/5.
 */
private const val TAG = "CrimeFragment"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_CODE_DATE = 1
private const val REQUEST_CODE_CONTACT = 2
private const val REQUEST_CODE_PERMISSION_READ_CONTACTS = 3
private const val REQUEST_CODE_PHOTO = 4
private const val ARG_CRIME_ID = "crime_id"
private const val DATE_FORMAT = "EEE,MMM,dd"

class CrimeFragment : Fragment(), DatePickerFragment.Callbacks {

    private lateinit var mCrime: Crime
    private lateinit var mRootView: View
    private lateinit var mTitleField: EditText
    private lateinit var mDateButton: Button
    private lateinit var mSolvedCheckBox: CheckBox
    private lateinit var mReportButton: Button
    private lateinit var mSuspectButton: Button
    private lateinit var mCallButton: Button
    private lateinit var mPhotoView: ImageView
    private lateinit var mPhotoButton: ImageButton
    private lateinit var mDateFormat: DateFormat

    private lateinit var mPhotoFile: File
    private lateinit var mPhotoUri: Uri


    private val mCrimeDetailViewModel: CrimeDetailViewModel by lazy {
        defaultViewModelProviderFactory.create(CrimeDetailViewModel::class.java)
    }

    private val mOnGlobalFocusChangeListener: ViewTreeObserver.OnGlobalFocusChangeListener =
        ViewTreeObserver.OnGlobalFocusChangeListener { oldFocus, newFocus ->
            mPhotoFile.let {
                val path = it.path
                val file = File(path)
                if (file.exists()) {
                    val bitmap = getScaledBitmap(it.path, mPhotoView.width, mPhotoView.height)
                    bitmap?.let {
                        mPhotoView.setImageBitmap(bitmap)
                    }

                }
            }
        }

    override fun onDateSelected(date: Date) {
        mCrime.date = date
        updateUI()
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
        mRootView = view.findViewById(R.id.view_root)
        mTitleField = view.findViewById<EditText>(R.id.crime_title)
        mDateButton = view.findViewById(R.id.crime_date)
        mReportButton = view.findViewById(R.id.crime_report)
        mSuspectButton = view.findViewById(R.id.crime_suspect)
        mCallButton = view.findViewById(R.id.crime_call)
        mSolvedCheckBox = view.findViewById(R.id.crime_solved)
        mPhotoView = view.findViewById(R.id.crime_photo)
        mPhotoButton = view.findViewById(R.id.crime_camera)

        mDateButton.apply {
            text = mDateFormat.format(mCrime.date)
            setOnClickListener {
                DatePickerFragment.newInstance(mCrime.date).apply {
                    setTargetFragment(this@CrimeFragment, REQUEST_CODE_DATE)
                    show(this@CrimeFragment.requireFragmentManager(), DIALOG_DATE)
                }
            }
        }



        mCrimeDetailViewModel.mCrimeLiveData
            .observe(viewLifecycleOwner,
                Observer { crime ->
                    crime?.let {
                        this.mCrime = crime
                        mPhotoFile = mCrimeDetailViewModel.getPhotoFile(crime)
                        mPhotoUri = getUriCompat(mPhotoFile)
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
        mReportButton.setOnClickListener {
            // TODO: 2021/6/6 apply && also
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also { intent ->
                startActivity(intent)
            }
        }
        mSuspectButton.apply {
            val pickIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            setOnClickListener {
                startActivityForResult(pickIntent, REQUEST_CODE_CONTACT)
            }
            val resolveActivity: ResolveInfo? = requireActivity().packageManager
                .resolveActivity(pickIntent, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolveActivity == null) {
                isEnabled = false
            }
        }
        mCallButton.setOnClickListener {
            if (grantedContactPermission()) {
                callPhone(getNumber())
            } else {
                val show = shouldShowRequestPermissionRationale()
                if (show) {
                    Snackbar.make(
                        mRootView,
                        R.string.tip_permission_read_contact,
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(android.R.string.ok) {
                        requestReadContactPermission()
                    }.show()
                } else {
                    requestReadContactPermission()
                }
            }
        }

        mPhotoButton.apply {
            val pm = requireActivity().packageManager
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolveActivity =
                pm.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolveActivity == null) {
                isEnabled = false
            }
            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri)
                val queryIntentActivities: List<ResolveInfo> =
                    pm.queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
                //动态授予一次写入权限
                for (cameraActivity in queryIntentActivities) {
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        mPhotoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
                startActivityForResult(captureImage, REQUEST_CODE_PHOTO)
            }
        }
        mPhotoView.setOnClickListener {
            if (!mPhotoFile.exists()) {
                return@setOnClickListener
            }
            val fragment = PhotoFragment.newInstance(mPhotoFile.path)
            fragmentManager?.let { it1 -> fragment.show(it1, PhotoFragment.TAG) }
        }
        mPhotoView.viewTreeObserver.addOnGlobalFocusChangeListener(mOnGlobalFocusChangeListener)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_DATE) {
            // TODO: 2021/6/6
        } else if (requestCode == REQUEST_CODE_CONTACT && data != null) {
            getSuspect(data)
        } else if (requestCode == REQUEST_CODE_PHOTO) {
            revokePermission()
            updatePhotoView()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult: ")
        if (requestCode == REQUEST_CODE_PERMISSION_READ_CONTACTS) {
            if (grantedContactPermission()) {
                Snackbar.make(
                    mRootView,
                    R.string.user_granted_permission,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    callPhone(getNumber())
                }.show()
                Log.d(TAG, "onRequestPermissionsResult: grantedContactPermission")
            } else if (grantResults.isEmpty()) {
                Snackbar.make(
                    mRootView,
                    R.string.user_cancel,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                }.show()
                Log.d(TAG, "onRequestPermissionsResult: isEmpty")
            } else {
                val shouldShow = shouldShowRequestPermissionRationale()
                Log.d(TAG, "onRequestPermissionsResult: shouldShow =$shouldShow")
                if (shouldShow) {
                    Snackbar.make(
                        mRootView,
                        R.string.tip_permission_read_contact,
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(android.R.string.ok) {
                        requestReadContactPermission()
                    }.show()
                } else {
                    // FIXME: 2021/6/7 本来用户点击拒绝且不再提示，应该走这里的，却没有调用到整个onRequestPermissionsResult
                    Log.d(TAG, "onRequestPermissionsResult:用户拒绝，且不再提示 ")
                    Snackbar.make(
                        mRootView,
                        R.string.user_refuse,
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(android.R.string.ok) {
                        openSettings()
                    }.show()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mCrimeDetailViewModel.saveCrime(mCrime)
        mPhotoView.viewTreeObserver.removeOnGlobalLayoutListener { mOnGlobalFocusChangeListener }
    }

    override fun onDetach() {
        super.onDetach()
        revokePermission()
    }

    private fun getSuspect(data: Intent) {
        val contactUri: Uri = data.data ?: return
        val queryFiled = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
        val cursor: Cursor = requireActivity().contentResolver.query(
            contactUri, queryFiled, null, null, null
        ) ?: return
        // TODO: 2021/6/6 use
        cursor.use {
            if (it.count == 0) {
                return
            }
            it.moveToFirst()
            val suspect = it.getString(0)
            mCrime.suspect = suspect
            mCrimeDetailViewModel.saveCrime(mCrime)
            //mSuspectButton.text = suspect
            updateUI()
            it.close()
        }
    }

    private fun getNumber(): String? {
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val cursor = requireActivity().contentResolver.query(
            uri, arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} = ?",
            arrayOf(mCrime.suspect),
            null
        ) ?: return null
        cursor.use {
            if (it.count == 0) {
                return null
            }
            it.moveToFirst()
            val number = it.getString(0)
            it.close()
            return number
        }
    }


    private fun requestReadContactPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_CONTACTS),
            REQUEST_CODE_PERMISSION_READ_CONTACTS
        )
    }

    private fun updateUI() {
        mTitleField.setText(mCrime.title)
        mDateButton.text = mDateFormat.format(mCrime.date)
        mSolvedCheckBox.apply {
            isChecked = mCrime.isSolved
            // TODO: 2021/6/6 跳过此次动画，不影响手动勾选的效果
            jumpDrawablesToCurrentState()
        }
        if (mCrime.suspect.isNotBlank()) {
            mSuspectButton.text = mCrime.suspect
            mCallButton.visibility = View.VISIBLE
        } else {
            mCallButton.visibility = View.GONE
        }
        updatePhotoView()
    }

    private fun updatePhotoView() {
        if (mPhotoFile.exists()) {
            val bitmap = getScaledBitmap(mPhotoFile.path, requireActivity())
            mPhotoView.setImageBitmap(bitmap)
            mPhotoView.contentDescription = getString(R.string.crime_photo_image_description)
        } else {
            mPhotoView.setImageBitmap(null)
            mPhotoView.contentDescription = getString(R.string.crime_photo_no_image_description)
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = getString(
            if (mCrime.isSolved) {
                R.string.crime_report_solved
            } else {
                R.string.crime_report_unsolved
            }
        )
        val dateString = android.text.format.DateFormat.format(
            DATE_FORMAT, mCrime.date
        ).toString()
        var suspect = if (mCrime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, mCrime.suspect)
        }
        return getString(R.string.crime_report, mCrime.title, dateString, solvedString, suspect)
    }

    /**
     * todo 15.5　挑战练习：又一个隐式intent 未完成
     */
    private fun callPhone(phone: String?) {
        if (phone == null) {
            return
        }
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val resolveActivity = requireActivity().packageManager
            .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (resolveActivity == null) {
            Toast.makeText(requireContext(), R.string.no_found_dialer, Toast.LENGTH_SHORT).show()
            return
        }
        startActivity(intent)
    }

    /**
     * 打开应用详细设置
     */
    private fun openSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
        }.also {
            startActivity(it)
        }
    }

    private fun grantedContactPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun shouldShowRequestPermissionRationale(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.READ_CONTACTS
        )
    }

    private fun getUriCompat(file: File): Uri {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(
                requireContext(),
                "${BuildConfig.APPLICATION_ID}.fileprovider",
                file
            )
        }
        return Uri.fromFile(file);
    }

    /**
     * 撤销权限
     */
    private fun revokePermission() {
        requireActivity().revokeUriPermission(
            mPhotoUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
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