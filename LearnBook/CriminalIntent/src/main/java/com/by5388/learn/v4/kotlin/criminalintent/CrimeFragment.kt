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
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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
private const val DATE_FORMAT = "EEE,MMM,dd"

class CrimeFragment : Fragment() {

    private lateinit var mCrime: Crime
    private lateinit var mDateFormat: DateFormat
    private lateinit var mTimeFormat: DateFormat
    private var _mBinding: FragmentCrimeBinding? = null
    private val mBinding: FragmentCrimeBinding
        get() = _mBinding!!

    private lateinit var mPhotoFile: File
    private lateinit var mPhotoUri: Uri
    private lateinit var mCrimeFragmentArgs: CrimeFragmentArgs

    /**
     * 读取联系人的名称
     */
    private val mContactResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            handleContactResult(it)
        }

    /**
     * 获取拍照的结果
     */
    private val mCapPhotoResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            handleCaptureResult(it)
        }

    /**
     * 读取联系人的号码
     */
    private val mContactPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            handleContactPermission(it)
        }


    private val mCrimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CrimeDetailViewModel::class.java)
    }
    var titleWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            mCrime.title = s.toString()
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
        arguments?.let {
            mCrimeFragmentArgs = CrimeFragmentArgs.fromBundle(it)
        }
        mDateFormat = SimpleDateFormat(
            context?.getString(
                R.string.date_format
            ), Locale.getDefault()
        )

        mTimeFormat = SimpleDateFormat(
            context?.getString(
                R.string.time_format
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
        mCrimeDetailViewModel.loadCrime(mCrimeFragmentArgs.crimeID)
        mBinding.crimeDate.apply {
            text = mDateFormat.format(mCrime.date)
            setOnClickListener {
                pickerDate()
            }
        }
        mBinding.crimeTime.apply {
            text = mTimeFormat.format(mCrime.date)
            setOnClickListener {
                pickerTime()
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
                mContactResultLauncher.launch(pickIntent)
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
                mCapPhotoResultLauncher.launch(captureImage)
            }
        }
        mBinding.crimePhoto.setOnClickListener {
            if (!mPhotoFile.exists()) {
                return@setOnClickListener
            }
            showPhotoDialog(mPhotoFile.path)
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
                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun handleContactPermission(granted: Boolean) {
        Log.d(TAG, "onRequestPermissionsResult: ")
        if (true) {
            if (granted) {
                Snackbar.make(
                    mBinding.viewRoot,
                    R.string.user_granted_permission,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    callPhone(getNumber())
                }.show()
                Log.d(TAG, "onRequestPermissionsResult: grantedContactPermission")
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
                    // 2021/6/7 本来用户点击拒绝且不再提示，应该走这里的，却没有调用到整个onRequestPermissionsResult
                    //  该问题已解决：在fragment中使用 ActivityCompat.requestPermissions，其结果只会回调在activity中而不会在fragment中回调
                    //
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
        mContactPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
    }

    private fun updateUI() {
        mBinding.crimeTitle.setText(mCrime.title)
        mBinding.crimeDate.text = mDateFormat.format(mCrime.date)
        mBinding.crimeTime.text = mTimeFormat.format(mCrime.date)
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
        val suspect = if (mCrime.suspect.isBlank()) {
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

    private fun showPhotoDialog(filePath: String) {
        val navController = findNavController()
        val build = PhotoFragmentArgs.Builder(filePath).build()
        navController.navigate(R.id.action_CrimeFragment_to_PhotoFragmentDialog, build.toBundle())
    }

    /**
     * 选择日期
     */
    private fun pickerDate() {
        val build = DatePickerDialogFragmentArgs.Builder(mCrime.id, mCrime.date).build()
        findNavController().navigate(
            R.id.action_CrimeFragment_to_DatePickerDialogFragment,
            build.toBundle()
        )
    }

    /**
     * 选择时间
     */
    private fun pickerTime() {
        val build = TimePickerDialogFragmentArgs.Builder(mCrime.id, mCrime.date).build()
        findNavController().navigate(
            R.id.action_CrimeFragment_to_TimePickerDialogFragment,
            build.toBundle()
        )
    }


    private fun handleContactResult(result: ActivityResult) {
        if (result.resultCode != Activity.RESULT_OK) {
            return
        }
        result.data?.let {
            getSuspect(it)
        }
    }

    private fun handleCaptureResult(result: ActivityResult) {
        if (result.resultCode != Activity.RESULT_OK) {
            return
        }
        result.data?.let {
            revokePermission()
            updatePhotoView()
        }
    }

}