package com.android.widgets

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.view.View

class SpannedTextView : AppCompatTextView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private var mText: String? = null

    fun init(attrs: AttributeSet?) {
        attrs?.apply {
            val typedArray = context.obtainStyledAttributes(this, R.styleable.SpannedTextView)
            mText = typedArray.getString(R.styleable.SpannedTextView_spannedText)
            typedArray.recycle()
        }
        if (mText == null) mText = ""
        setText(SpannableString(mText), BufferType.SPANNABLE)
        movementMethod = LinkMovementMethod.getInstance()
    }

    fun underlineAll() {
        (text as Spannable).setSpan(object : UnderlineSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = resources.getColor(R.color.blue_light)
            }
        }, 0, mText!!.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    fun underline(spannedText: String): SpannedTextView {
        val indexOfSpanned = mText!!.indexOf(spannedText)
        (text as Spannable).setSpan(UnderlineSpan(),
            indexOfSpanned,
            indexOfSpanned + spannedText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return this
    }

    fun onClick(spannedText: Int, function: () -> Unit) {
        onClick(resources.getString(spannedText), function)
    }

    fun onClick(spannedText: String, function: () -> Unit) {
        val indexOfSpanned = mText!!.indexOf(spannedText)
        onClick(indexOfSpanned, indexOfSpanned + spannedText.length, function)
    }

    fun onClick(function: () -> Unit) {
        onClick(0, text.length, function)
    }

    fun onClick(offsetStart: Int, offsetEnd: Int, function: () -> Unit) {
        (text as Spannable).setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                function.invoke()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = resources.getColor(R.color.blue_light)
            }
        }, offsetStart, offsetEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}