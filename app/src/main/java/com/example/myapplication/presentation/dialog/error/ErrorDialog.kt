package com.example.myapplication.presentation.dialog.error

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.myapplication.R

class ErrorDialog(private val context: Context) {
    private val dialog: AlertDialog by lazy {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
            .setView(R.layout.error_dialog)
            .setNegativeButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create()
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}