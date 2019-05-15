package com.kantek.yelp.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.util.AttributeSet
import android.widget.TextView
import com.kantek.yelp.R
import com.kantek.yelp.extensions.loadAttrs

class AppTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {
    private var mDrawableColor: Int = 0

    init {
        context.loadAttrs(attrs, R.styleable.AppTextView) {
            mDrawableColor = it.getColor(R.styleable.AppTextView_drawableColor, 0)
        }
        applyColor()
    }

    fun setDrawableColorResource(@ColorRes colorId: Int) {
        mDrawableColor = resources.getColor(colorId)
        applyColor()
        invalidate()
    }

    override fun setCompoundDrawables(left: Drawable?, top: Drawable?, right: Drawable?, bottom: Drawable?) {
        applyColor(left, top, right, bottom)
        super.setCompoundDrawables(left, top, right, bottom)
    }

    private fun applyColor() {
        if (mDrawableColor == 0) return
        compoundDrawablesRelative.forEach { it?.applyColor(mDrawableColor) }
    }

    private fun applyColor(left: Drawable?, top: Drawable?, right: Drawable?, bottom: Drawable?) {
        if (mDrawableColor == 0) return
        left?.applyColor(mDrawableColor)
        top?.applyColor(mDrawableColor)
        right?.applyColor(mDrawableColor)
        bottom?.applyColor(mDrawableColor)
    }

    override fun setCompoundDrawablesRelative(start: Drawable?, top: Drawable?, end: Drawable?, bottom: Drawable?) {
        applyColor(start, top, end, bottom)
        super.setCompoundDrawablesRelative(start, top, end, bottom)
    }

    override fun setCompoundDrawableTintList(tint: ColorStateList?) {
        throw RuntimeException("Not support drawable tint, using drawable color for alternative")
    }
}

private fun Drawable.applyColor(color: Int) {
    colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
}
