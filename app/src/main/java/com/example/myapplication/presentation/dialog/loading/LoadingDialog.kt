package com.example.myapplication.presentation.dialog.loading

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.myapplication.R

class LoadingDialog(private val context: Context) {
    private val dialog: AlertDialog by lazy {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Loading")
            .setView(R.layout.loading_dialog)
            .setCancelable(false)
        builder.create()
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}