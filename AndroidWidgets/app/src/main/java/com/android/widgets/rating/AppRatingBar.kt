package com.android.widgets.rating

import android.content.Context
import android.util.AttributeSet
import com.android.widgets.R


class AppRatingBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : StarRatingBar(context, attrs, defStyleAttr) {
    override var rating: Float
        get() = super.rating
        set(value) {
            ratingColor = resources.getColor(when {
                value > 4f && value <= 5f -> R.color.colorPrimary
                value > 3f && value <= 4f -> R.color.green_yellow
                value > 2f && value <= 3f -> R.color.green
                value > 1f && value <= 2f -> R.color.orange
                else -> R.color.red
            })
            super.rating = value
        }
}
