package com.android.widgets.rating

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.support.annotation.StyleableRes
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.android.widgets.R


open class StarRatingBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        const val NUM_OF_STAR = 5
    }

    var shouldTouchToChange = false
    private var mRating = 2f
    private var mRatingBackgroundColor: Int = Color.GRAY
        set(value) {
            field = value
            mStars.forEach { it.backgroundColor = value }
        }
    private var mRatingColor: Int = Color.BLUE
        set(value) {
            field = value
            mStars.forEach { it.color = value }
        }
    private var mStars = (0..NUM_OF_STAR).map { StarDrawable() }

    var ratingColor
        get() = mRatingColor
        set(value) {
            mRatingColor = value
        }

    var ratingBackgroundColor
        get() = mRatingBackgroundColor
        set(value) {
            mRatingBackgroundColor = value
        }

    open var rating
        get() = mRating
        set(value) {
            mRating = value
            mStars.forEachIndexed { index, starDrawable ->
                applyRating(index, starDrawable)
            }
            invalidate()
        }

    init {
        context.loadAttrs(attrs, R.styleable.StarRatingBar) {
            mRatingBackgroundColor = it.getColor(R.styleable.StarRatingBar_ratingBackgroundColor, Color.GRAY)
            mRatingColor = it.getColor(R.styleable.StarRatingBar_ratingColor, Color.BLUE)
            mRating = it.getFloat(R.styleable.StarRatingBar_rating, 2f)
        }
        mStars.forEach {
            it.color = mRatingColor
            it.backgroundColor = mRatingBackgroundColor
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mStars.forEachIndexed { index, item ->
            item.layout(index, if (index == 0) 0 else paddingStart)
        }
        rating = mRating
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val size = (width - ((NUM_OF_STAR - 1) * paddingStart)) / NUM_OF_STAR
        mStars.forEach { it.measure(size) }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!shouldTouchToChange) return super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_MOVE -> {
                parent.requestDisallowInterceptTouchEvent(true)
                mStars.findSelectedAt(event.x)?.also {
                    val tmpRating = it.calculateRating(event.x, 0f, NUM_OF_STAR.toFloat())
                    if (tmpRating == rating) return@also
                    rating = tmpRating
                    invalidate()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun applyRating(index: Int, item: StarDrawable) {
        val distance = mRating - index
        item.percent = when {
            distance == 0f || distance >= 1f -> 1f
            distance > 0f && distance < 1f -> Math.abs(distance)
            else -> 0f
        }
    }

    override fun onDraw(canvas: Canvas) {
        mStars.forEach { it.draw(canvas) }
    }
}

private fun List<StarDrawable>.findSelectedAt(x: Float) = find {
    it.offsetLeft <= x && it.offsetRight >= x
}

private fun Context.loadAttrs(attrs: AttributeSet?, @StyleableRes styleable: IntArray, function: (TypedArray) -> Unit) {
    val a = obtainStyledAttributes(attrs, styleable)
    function(a)
    a.recycle()
}
