package com.android.widgets

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet

class SquaresTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = measuredHeight
        val width = measuredWidth
        val spec = MeasureSpec.makeMeasureSpec(Math.max(width, height), MeasureSpec.EXACTLY)
        super.onMeasure(spec, spec)
    }
}