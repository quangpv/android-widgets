package com.android.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition


class SignatureView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_COLOR = Color.BLACK
        private const val DEFAULT_TOUCH_TOLERANCE = 5f
        private const val DEFAULT_LINE_THICKNESS = 5f
    }

    private var mBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private var mPath = Path()
    private var mBitmapPaint = Paint(Paint.DITHER_FLAG)
    private var mPaint = Paint()
    private var mX: Float = 0.toFloat()
    private var mY: Float = 0.toFloat()
    private var mTolerance = DEFAULT_TOUCH_TOLERANCE
    private var mThickness = DEFAULT_LINE_THICKNESS
    private var mColor = DEFAULT_COLOR
    private val mPadding = resources.getDimension(R.dimen.size_5)

    var signature: Bitmap?
        set(value) {
            if (value == null) return
            clearScreen()
            AppExecutors
                .loadInBackGround {
                    value.trim()?.scaleByHeight(measuredHeight, mPadding)
                }.postOnUi {
                    if (it == null) return@postOnUi
                    drawBitmap(it)
                    it.recycle()
                }
        }
        get() = getDrawing()

    var isForceDisabled: Boolean = false

    private fun drawBitmap(value: Bitmap) {
        mCanvas?.drawColor(0, PorterDuff.Mode.CLEAR)
        mCanvas?.drawBitmap(value,
            (measuredWidth / 2 - value.width / 2).toFloat(),
            (measuredHeight / 2 - value.height / 2).toFloat(),
            mBitmapPaint)
        invalidate()
    }

    init {
        loadAttrsIfNeeded(attrs)
        setupPaint()
    }

    private fun loadAttrsIfNeeded(attrs: AttributeSet?) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SignatureView)
        mColor = typedArray.getColor(R.styleable.SignatureView_drawingColor, DEFAULT_COLOR)
        mThickness = typedArray.getDimension(R.styleable.SignatureView_drawingThickness, DEFAULT_LINE_THICKNESS)
        mTolerance = typedArray.getDimension(R.styleable.SignatureView_drawingTolerance, DEFAULT_TOUCH_TOLERANCE)
        typedArray.recycle()
    }

    private fun setupPaint() {
        mPaint.apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = mThickness
            color = mColor
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBitmap = Bitmap.createBitmap(w, if (h > 0) h else (parent as View).height, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mBitmap!!, 0f, 0f, mBitmapPaint)
        canvas.drawPath(mPath, mPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (isForceDisabled) return false
        if (!isEnabled) return false
        parent?.requestDisallowInterceptTouchEvent(true)
        super.onTouchEvent(e)
        val x = e.x
        val y = e.y

        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                startTouch(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                moveTouch(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                endTouch()
                invalidate()
            }
        }

        return true
    }

    /**
     * Reset the drawing view by cleaning the canvas that contains the drawing
     */
    fun clearScreen() {
        mCanvas!!.drawColor(0, PorterDuff.Mode.CLEAR)
        invalidate()
    }

    /**
     * Let you retrieve the drawing that has been drawn inside the canvas
     *
     * @return The drawing as a Bitmap
     */
    private fun getDrawing(): Bitmap {
        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        layout(left, top, right, bottom)
        draw(c)

        return b
    }

    /**
     * @return Original bitmap drawn
     */
    fun getBitmap() = mBitmap

    private fun startTouch(x: Float, y: Float) {
        mPath.reset()
        mPath.moveTo(x, y)
        mX = x
        mY = y
    }

    private fun moveTouch(x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy = Math.abs(y - mY)

        if (dx >= mTolerance || dy >= mTolerance) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }

    private fun endTouch() {
        if (!mPath.isEmpty) {
            mPath.lineTo(mX, mY)
            mCanvas!!.drawPath(mPath, mPaint)
        } else {
            mCanvas!!.drawPoint(mX, mY, mPaint)
        }

        mPath.reset()
    }

    fun setImageUrl(imageUrl: String) {
        Glide.with(this)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .load(imageUrl)
            .override(width, height)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    signature = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }
}

fun Bitmap.scaleByHeight(maxHeight: Int, padding: Float): Bitmap {
    val bitmapResize: Bitmap
    bitmapResize = if (height > maxHeight) {
        val ratio = (height.toFloat() + padding * 2) / maxHeight.toFloat()
        val newBmp = Bitmap.createScaledBitmap(this,
            (width / ratio).toInt(),
            (height / ratio).toInt(), false)
        recycle()
        newBmp
    } else this
    return bitmapResize
}

fun Bitmap.trim(): Bitmap? {
    var minX = width
    var minY = height
    var maxX = -1
    var maxY = -1
    for (y in 0 until height) {
        for (x in 0 until width) {
            val alpha = getPixel(x, y) shr 24 and 255
            if (alpha > 0) {
                if (x < minX)
                    minX = x
                if (x > maxX)
                    maxX = x
                if (y < minY)
                    minY = y
                if (y > maxY)
                    maxY = y
            }
        }
    }
    return if (maxX < minX || maxY < minY) null else Bitmap.createBitmap(this, minX, minY, maxX - minX + 1, maxY - minY + 1)
}
