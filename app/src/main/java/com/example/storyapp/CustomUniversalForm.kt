package com.example.storyapp

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class CustomUniversalForm: AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun init() {
        setOnFocusChangeListener { _, hasFocus ->
            background = if (hasFocus) {
                resources.getDrawable(R.drawable.bg_edit_text_active, null)
            } else {
                resources.getDrawable(R.drawable.input_form, null)
            }
        }
    }
}