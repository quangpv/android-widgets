package com.android.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.TextView

class LimitCharEditText : AppCompatEditText {
    companion object {
        const val MAX_CHAR = 300
    }

    private val mTextWatchers = arrayListOf<TextWatcher>()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, theme: Int) : super(context, attrs, theme)

    var maxChar: Int = MAX_CHAR

    fun setupRemainChangedWithView(view: TextView) {
        setText("")
        display(view, maxChar)
        addTextChangedListener(object : TextWatcher {
            private var mPreviousText = ""
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                mPreviousText = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val remain = maxChar - s!!.length
                if (remain < 0) setTextSilent(mPreviousText)
                else display(view, remain)
            }
        })
    }

    fun setTextSilent(value: String) {
        mTextWatchers.forEach { super.removeTextChangedListener(it) }
        setText(value)
        setSelection(value.length)
        mTextWatchers.forEach { super.addTextChangedListener(it) }
    }

    override fun addTextChangedListener(watcher: TextWatcher?) {
        watcher?.apply { mTextWatchers.add(this) }
        super.addTextChangedListener(watcher)
    }

    override fun removeTextChangedListener(watcher: TextWatcher?) {
        watcher?.apply { mTextWatchers.remove(this) }
        super.removeTextChangedListener(watcher)
    }

    @SuppressLint("SetTextI18n")
    fun display(view: TextView, remain: Int) {
        view.text = "$remain Chars left"
    }
}
