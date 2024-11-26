package com.example.myapplication.presentation.dialog.error

import android.content.Context
import android.view.LayoutInflater
import android.webkit.ConsoleMessage
import androidx.appcompat.app.AlertDialog
import com.example.myapplication.R
import com.example.myapplication.databinding.ErrorDialogBinding

class ErrorDialog(private val context: Context, private val message: String) {
    constructor(context: Context) : this(context, message = context.getString(R.string.something_was_wrong_when_we_try_to_loading_your_data_please_try_again_now_or_some_time_late))
    private val dialog: AlertDialog by lazy {
        val builder = AlertDialog.Builder(context)
        val binding = ErrorDialogBinding.inflate(LayoutInflater.from(context))
        binding.itemError.text = message
        builder.setTitle("Error")
            .setView(binding.root)
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