package com.example.storyapp

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class CustomEmailForm: AppCompatEditText {

    constructor(context: Context): super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr){
        init()
    }

    private fun init(){
        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                background = resources.getDrawable(R.drawable.bg_edit_text_active, null)
            }else{
                background = resources.getDrawable(R.drawable.input_form, null)
            }
        }

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

                if (!s.toString().contains("@")) {
                    error = "Email must be valid"
                } else {
                    error = null
                }
            }

        })
    }




}