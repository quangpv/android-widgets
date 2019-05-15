package com.android.widgets.rating

import android.graphics.*
import android.graphics.drawable.Drawable

class StarDrawable : Drawable() {
    private var mPadding: Int = 0
    private var mIndex: Int = 0
    private val mPath = Path()
    private var mSize = 30f
    var color: Int = Color.BLACK
    var backgroundColor: Int = Color.GRAY
    var percent = 0f

    private val offsetTop get() = mPaint.strokeWidth
    val offsetRight get() = offsetLeft + mSize
    val offsetLeft get() = mIndex * (mSize + mPadding)
    private lateinit var mBitmap: Bitmap
    private lateinit var mCanvas: Canvas

    private val mPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.BLACK
        isAntiAlias = true
    }
    private val mFillPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }

    override fun draw(canvas: Canvas) {
        makeBitmap()
        canvas.drawBitmap(mBitmap, offsetLeft, offsetTop, null)
    }

    private fun makeBitmap() {
        mPaint.color = if (percent == 1f) color else backgroundColor
        mCanvas.drawPath(mPath, mPaint)
        if (percent < 1f && percent > 0) {
            drawPercent(mSize * percent, mSize)
        }
    }

    private fun drawPercent(width: Float, height: Float) {
        if (width <= 0 || height <= 0) return
        mFillPaint.color = color
        mCanvas.drawRect(0f, 0f, width, height, mFillPaint)
    }

    fun measure(size: Int) {
        mSize = size - mPaint.strokeWidth * 2
        mBitmap = Bitmap.createBitmap(mSize.toInt(), mSize.toInt(), Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap)
    }

    fun layout(index: Int, padding: Int) {
        mIndex = index
        mPadding = padding
        mPath.makeStar(mSize.toInt())
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun getOpacity() = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
    }

    fun calculateRating(x: Float, min: Float, max: Float) =
        Math.max(min, Math.min(max, (x - offsetLeft) / mSize + mIndex))
}

private fun Path.makeStar(size: Int) {
    val bigHypot = size / Math.cos(Math.toRadians(18.0))
    val bigB = size
    val bigA = Math.tan(Math.toRadians(18.0)) * bigB

    val littleHypot = bigHypot / (2 + Math.cos(Math.toRadians(72.0)) + Math.cos(Math.toRadians(72.0)))
    val littleA = Math.cos(Math.toRadians(72.0)) * littleHypot
    val littleB = Math.sin(Math.toRadians(72.0)) * littleHypot

    val topXPoint = size.toFloat() / 2
    val topYPoint = 0f

    moveTo(topXPoint, topYPoint)
    lineTo((topXPoint + bigA).toFloat(), (topYPoint + bigB))
    lineTo((topXPoint - littleA - littleB).toFloat(), (topYPoint + littleB).toFloat())
    lineTo((topXPoint + littleA + littleB).toFloat(), (topYPoint + littleB).toFloat())
    lineTo((topXPoint - bigA).toFloat(), (topYPoint + bigB))
    lineTo(topXPoint, topYPoint)
    close()
}