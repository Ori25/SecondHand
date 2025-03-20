package com.example.androidproject.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class ImagePickerFragment : Fragment() {

    abstract val onImagePicked: (Uri?) -> Unit
    protected var selectedImage: Uri? = null

    protected val mGetImage: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            selectedImage = it
            onImagePicked(it)
        }

    protected val mPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                openGallery()
            }
        }

    protected fun openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
            mGetImage.launch("image/*")
        } else {
            if (requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                mPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                mGetImage.launch("image/*")
            }
        }
    }
}