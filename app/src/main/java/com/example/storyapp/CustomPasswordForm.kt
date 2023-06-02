package com.example.storyapp

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class CustomPasswordForm : AppCompatEditText {
    private val passwordTransformationMethod = PasswordTransformationMethod.getInstance()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        transformationMethod = passwordTransformationMethod

        setOnFocusChangeListener{ _, hasFocus ->
            if (hasFocus) {
                background = resources.getDrawable(R.drawable.bg_edit_text_active, null)
            } else {
                background = resources.getDrawable(R.drawable.input_form, null)
            }
        }

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

                if (s?.length ?: 0 < 8) {
                    error = "Password must be at least 8 characters long"
                } else {
                    error = null
                }
            }

        })
    }


}