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
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.by5388.learn.v4.kotlin.criminalintent.databinding.FragmentCrimeBinding
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

class CrimeFragment : Fragment() {

    private lateinit var mCrime: Crime
    private lateinit var mDateFormat: DateFormat
    private var _mBinding: FragmentCrimeBinding? = null
    private val mBinding: FragmentCrimeBinding
        get() = _mBinding!!

    private lateinit var mPhotoFile: File
    private lateinit var mPhotoUri: Uri


    private val mCrimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CrimeDetailViewModel::class.java)
    }
    var titleWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            mCrime?.title = s.toString()
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

//    private var _mOnGlobalFocusChangeListener: ViewTreeObserver.OnGlobalFocusChangeListener? = null
//
//    private val mOnGlobalFocusChangeListener: ViewTreeObserver.OnGlobalFocusChangeListener
//        get() = _mOnGlobalFocusChangeListener!!

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
    ): View {
        _mBinding = FragmentCrimeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.crimeDate.apply {
            text = mDateFormat.format(mCrime.date)
            setOnClickListener {
                DatePickerFragment.newInstance(mCrime.date, mCrime.id).apply {
                    //setTargetFragment(this@CrimeFragment, REQUEST_CODE_DATE)
                    show(this@CrimeFragment.requireFragmentManager(), DIALOG_DATE)
                }
            }
        }



        mCrimeDetailViewModel.mCrimeLiveData
            .observe(viewLifecycleOwner) { crime ->
                crime?.let {
                    this.mCrime = it
                    mPhotoFile = mCrimeDetailViewModel.getPhotoFile(it)
                    mPhotoUri = getUriCompat(mPhotoFile)
                    updateUI()
                }
            }

        mBinding.crimeTitle.addTextChangedListener(titleWatcher)

        mBinding.crimeSolved.apply {
            setOnCheckedChangeListener { _, isChecked ->
                mCrime.isSolved = isChecked
            }
        }
        mBinding.crimeReport.setOnClickListener {
            // TODO: 2021/6/6 apply && also
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also { intent ->
                startActivity(intent)
            }
        }
        mBinding.crimeSuspect.apply {
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
        mBinding.crimeCall.setOnClickListener {
            if (grantedContactPermission()) {
                callPhone(getNumber())
            } else {
                val show = shouldShowRequestPermissionRationale()
                if (show) {
                    Snackbar.make(
                        mBinding.viewRoot,
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

        mBinding.crimeCamera.apply {
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
        mBinding.crimePhoto.setOnClickListener {
            if (!mPhotoFile.exists()) {
                return@setOnClickListener
            }
            val fragment = PhotoFragment.newInstance(mPhotoFile.path)
            fragmentManager?.let { it1 -> fragment.show(it1, PhotoFragment.TAG) }
        }

//        _mOnGlobalFocusChangeListener = ViewTreeObserver.OnGlobalFocusChangeListener { _, _ ->
//            mPhotoFile.let {
//                val path = it.path
//                val file = File(path)
//                if (file.exists()) {
//                    val bitmap = getScaledBitmap(
//                        it.path,
//                        mBinding.crimePhoto.width,
//                        mBinding.crimePhoto.height
//                    )
//                    bitmap?.let {
//                        mBinding.crimePhoto.setImageBitmap(bitmap)
//                    }
//                }
//            }
//        }
//
//        Log.d(TAG, "onViewCreated: ")
//        mBinding.viewRoot.viewTreeObserver.addOnGlobalFocusChangeListener(
//            mOnGlobalFocusChangeListener
//        )

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                Log.d(TAG, "onOptionsItemSelected: 返回")
                NavHostFragment.findNavController(this@CrimeFragment)
                    .popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }


    @Deprecated("Deprecated in Java")
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

    @Deprecated("Deprecated in Java")
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
                    mBinding.viewRoot,
                    R.string.user_granted_permission,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    callPhone(getNumber())
                }.show()
                Log.d(TAG, "onRequestPermissionsResult: grantedContactPermission")
            } else if (grantResults.isEmpty()) {
                Snackbar.make(
                    mBinding.viewRoot,
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
                        mBinding.viewRoot,
                        R.string.tip_permission_read_contact,
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(android.R.string.ok) {
                        requestReadContactPermission()
                    }.show()
                } else {
                    // FIXME: 2021/6/7 本来用户点击拒绝且不再提示，应该走这里的，却没有调用到整个onRequestPermissionsResult
                    Log.d(TAG, "onRequestPermissionsResult:用户拒绝，且不再提示 ")
                    Snackbar.make(
                        mBinding.viewRoot,
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
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView: remove")
        mBinding.crimePhoto.setImageBitmap(null)
//        mBinding.viewRoot.viewTreeObserver.removeOnGlobalLayoutListener { mOnGlobalFocusChangeListener }
        mBinding.crimeTitle.removeTextChangedListener(titleWatcher)
//        _mOnGlobalFocusChangeListener = null
        _mBinding = null
        super.onDestroyView()
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
        mBinding.crimeTitle.setText(mCrime.title)
        mBinding.crimeDate.text = mDateFormat.format(mCrime.date)
        mBinding.crimeSolved.apply {
            isChecked = mCrime.isSolved
            // TODO: 2021/6/6 跳过此次动画，不影响手动勾选的效果
            jumpDrawablesToCurrentState()
        }
        if (mCrime.suspect.isNotBlank()) {
            mBinding.crimeSuspect.text = mCrime.suspect
            mBinding.crimeCall.visibility = View.VISIBLE
        } else {
            mBinding.crimeCall.visibility = View.GONE
        }
        updatePhotoView()
    }

    private fun updatePhotoView() {
        if (mPhotoFile.exists()) {
            val bitmap = getScaledBitmap(mPhotoFile.path, requireActivity())
            mBinding.crimePhoto.setImageBitmap(bitmap)
            mBinding.crimePhoto.contentDescription =
                getString(R.string.crime_photo_image_description)
        } else {
            mBinding.crimePhoto.setImageBitmap(null)
            mBinding.crimePhoto.contentDescription =
                getString(R.string.crime_photo_no_image_description)
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
        fun newBundle(crimeId: UUID): Bundle {
            return Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
        }
    }
}