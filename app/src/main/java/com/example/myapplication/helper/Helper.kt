package com.example.myapplication.helper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.myapplication.R
import java.net.URL

fun loadImageIntoImageView(context: Context, imageView: ImageView, uri: Uri) {
    val loadingDrawable = CircularProgressDrawable(context).apply {
        strokeWidth = 5f
        centerRadius = 30f
        start()
    }
    Glide.with(context).load(uri).centerCrop().placeholder(loadingDrawable).error(
        R.drawable.error_image_photo_icon).into(imageView)
}



fun getSingleImageBuilder(fragment: Fragment, onImageSelected: (Uri?) -> Unit): ActivityResultLauncher<PickVisualMediaRequest> {
    val pickMedia = fragment.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        onImageSelected(uri) // Gọi callback với URI đã chọn
    }
    return pickMedia
}

fun getSingleImageBuilder(activity: AppCompatActivity, onImageSelected: (Uri?) -> Unit): ActivityResultLauncher<PickVisualMediaRequest> {
    val pickMedia = activity.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        onImageSelected(uri) // Gọi callback với URI đã chọn
    }

    return pickMedia
}

fun getMultiImageBuilder(fragment: Fragment, onImagesSelected: (List<Uri>) -> Unit): ActivityResultLauncher<PickVisualMediaRequest> {
    val pickMultipleMedia = fragment.registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        onImagesSelected(uris) // Gọi callback với danh sách URI đã chọn
    }
    return pickMultipleMedia
}

fun getMultiImageBuilder(activity: AppCompatActivity, onImagesSelected: (List<Uri>) -> Unit): ActivityResultLauncher<PickVisualMediaRequest> {
    val pickMultipleMedia = activity.registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        onImagesSelected(uris) // Gọi callback với danh sách URI đã chọn
    }

    return pickMultipleMedia
}

fun checkAndRequestPermission(activity: AppCompatActivity) {
    val requestPermissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted->
        if (!isGranted) {
            Toast.makeText(activity.applicationContext, "Permission denied. Unable to upload images.", Toast.LENGTH_SHORT).show()
        }

    }
    val permission = if (Build.VERSION.SDK_INT >= 33) {
        Manifest.permission.READ_MEDIA_IMAGES
    }
    else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    when {
        ContextCompat.checkSelfPermission(activity.applicationContext, permission) == PackageManager.PERMISSION_GRANTED-> {

        }
        ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
            val dialogBuilder = AlertDialog.Builder(activity.applicationContext)
            dialogBuilder.setCancelable(false)
                .setMessage("We need access to your storage to upload images for personal profile information or product listings.")
                .setPositiveButton("Allow") { _,_->
                    requestPermissionLauncher.launch(permission)
                }
                .setNegativeButton("Deny") { dialog, _->
                    dialog.dismiss()
                }
            val dialog = dialogBuilder.create()
            dialog.show()
        }
        else->{
            requestPermissionLauncher.launch(permission)
        }
    }
}

fun checkAndRequestPermission(fragment: Fragment) {
    val requestPermissionLauncher = fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted->
        if (!isGranted) {
            Toast.makeText(fragment.requireContext(), "Permission denied. Unable to upload images.", Toast.LENGTH_SHORT).show()
        }

    }
    val permission = if (Build.VERSION.SDK_INT >= 33) {
        Manifest.permission.READ_MEDIA_IMAGES
    }
    else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    when {
        ContextCompat.checkSelfPermission(fragment.requireContext(), permission) == PackageManager.PERMISSION_GRANTED-> {
        }
        ActivityCompat.shouldShowRequestPermissionRationale(fragment.requireActivity(), permission) -> {
            val dialogBuilder = AlertDialog.Builder(fragment.requireContext())
            dialogBuilder.setCancelable(false)
                .setMessage("We need access to your storage to upload images for personal profile information or product listings.")
                .setPositiveButton("Allow") { _,_->
                    requestPermissionLauncher.launch(permission)
                }
                .setNegativeButton("Deny") { dialog, _->
                    dialog.dismiss()
                }
            val dialog = dialogBuilder.create()
            dialog.show()
        }
        else->{
            requestPermissionLauncher.launch(permission)
        }
    }
}


