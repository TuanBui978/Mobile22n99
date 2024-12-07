package com.example.myapplication.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatRadioButton


class ToggleableRadioButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): AppCompatRadioButton(context, attrs, defStyleAttr) {
    override fun toggle() {
        if (isChecked) {
            if (parent is RadioGroup) {
                (parent as RadioGroup).clearCheck()
            }
        } else {
            isChecked = true
        }
    }
}