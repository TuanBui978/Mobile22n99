package com.example.myapplication.presentation.dialog.datetime

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment

class DateTimePickerDialog(private val day: Int? = null,private val month: Int? = null,private val year: Int? = null) : DialogFragment( ), DatePickerDialog.OnDateSetListener {

    private var onDateSet: (day: Int, month: Int, year: Int)->Unit = {_,_,_->}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker.
        val c = Calendar.getInstance()
        val day = day?: c.get(Calendar.DAY_OF_MONTH)
        val month = month?:c.get(Calendar.MONTH)
        val year = year?: c.get(Calendar.YEAR)
        // Create a new instance of DatePickerDialog and return it.
        return DatePickerDialog(requireContext(), this, year, month, day)
    }
    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        onDateSet(day, month, year)
    }
    fun setOnDateSetListener(onDateSet: (day: Int, month: Int, year: Int)->Unit) {
        this.onDateSet = onDateSet
    }
}