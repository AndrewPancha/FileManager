package com.example.filemanager.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.filemanager.BuildConfig
import com.example.filemanager.R
import com.example.filemanager.databinding.ActivityMainBinding
import com.example.filemanager.model.FileModel
import com.example.filemanager.model.FileType

class MainActivity : AppCompatActivity(), OnFileClickListener {

    companion object {

        private const val USER_ASKED_PERMISSION_BEFORE = "permissions_already_asked"
        private const val ASK_ALL_FILES_ACCESS = "ask_all_files_access"
        private const val SHARED_PREFERENCES = "main_permissions"

        private val PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
    }

    private var alertDialog: AlertDialog? = null

    private val sharedPreferences by lazy {
        getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            requestPermissions()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissions()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
            && !Environment.isExternalStorageManager()
            && sharedPreferences.getBoolean(ASK_ALL_FILES_ACCESS, false)
        ) {
            showDialogToProvideAllFilesPermission()
        }

        if (savedInstanceState == null) {
            val filesListFragment =
                FilesListFragment.newInstance(Environment.getExternalStorageDirectory().path)

            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, filesListFragment)
                .addToBackStack(Environment.getExternalStorageDirectory().path)
                .commit()
        }

        binding.etFilesFilter.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addSearchFileFragment(v.text.toString())
                true
            } else false
        }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(
                this,
                PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            alertDialog?.dismiss()
        } else {
            if (alertDialog?.isShowing != true) {
                showDialogToProvidePermissions()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }

    override fun onClick(fileModel: FileModel) {
        if (fileModel.fileType == FileType.FOLDER) {
            addFileFragment(fileModel)
        }
    }

    private fun addSearchFileFragment(search: String) {
        val searchFilesListFragment = SearchFilesListFragment.newInstance(search)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container_view, searchFilesListFragment)
        fragmentTransaction.addToBackStack(search)
        fragmentTransaction.commit()
    }

    private fun addFileFragment(fileModel: FileModel) {
        val filesListFragment = FilesListFragment.newInstance(fileModel.path)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container_view, filesListFragment)
        fragmentTransaction.addToBackStack(fileModel.path)
        fragmentTransaction.commit()
    }

    private fun requestPermissions() {
        val userAskedPermissionBefore =
            sharedPreferences.getBoolean(USER_ASKED_PERMISSION_BEFORE, false)
        val requiredPermissions =
            ContextCompat.checkSelfPermission(this, PERMISSION) == PackageManager.PERMISSION_GRANTED

        if (requiredPermissions) {
            return
        }

        if (requiredPermissions &&
            shouldShowRequestPermissionRationale(PERMISSION)
        ) {
            requestPermission.launch(PERMISSION)
            return
        }

        if (userAskedPermissionBefore) {
            showDialogToProvidePermissions()
            return
        }

        requestPermission.launch(PERMISSION)
        sharedPreferences.edit()
            .putBoolean(USER_ASKED_PERMISSION_BEFORE, true)
            .apply()

    }

    private fun showDialogToProvidePermissions() {
        alertDialog?.dismiss()
        alertDialog = AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle(getString(R.string.grant_permission))
            .setMessage(getString(R.string.provide_permission_text))
            .setPositiveButton(getString(R.string.go_to_settings)) { _, _ ->
                startActivity(
                    Intent()
                        .setData(Uri.fromParts("package", packageName, null))
                        .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                )
            }
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                finish()
            }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun showDialogToProvideAllFilesPermission() {
        AlertDialog.Builder(this)
            .setCancelable(true)
            .setTitle(getString(R.string.grant_permission))
            .setMessage(getString(R.string.all_files_permission_dialog_text))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                startActivity(
                    Intent()
                        .setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID))
                        .setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                )
            }
            .setNegativeButton(getString(R.string.no)) { dialog, value ->
                sharedPreferences.edit()
                    .putBoolean(ASK_ALL_FILES_ACCESS, true)
                    .apply()
                dialog.dismiss()
            }
            .show()
    }
}
