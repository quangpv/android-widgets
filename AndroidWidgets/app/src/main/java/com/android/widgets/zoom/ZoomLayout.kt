package com.android.widgets.zoom


import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.support.annotation.AttrRes
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.android.widgets.R

class ZoomLayout private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    val engine: ZoomEngine = ZoomEngine(context)
) : FrameLayout(context, attrs, defStyleAttr),
    ZoomApi by engine {

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0)
            : this(context, attrs, defStyleAttr, ZoomEngine(context))

    private var hasClickableChildren: Boolean = false

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ZoomEngine, defStyleAttr, 0)
        val overScrollHorizontal = a.getBoolean(R.styleable.ZoomEngine_overScrollHorizontal, true)
        val overScrollVertical = a.getBoolean(R.styleable.ZoomEngine_overScrollVertical, true)
        val horizontalPanEnabled = a.getBoolean(R.styleable.ZoomEngine_horizontalPanEnabled, true)
        val verticalPanEnabled = a.getBoolean(R.styleable.ZoomEngine_verticalPanEnabled, true)
        val overPinchable = a.getBoolean(R.styleable.ZoomEngine_overPinchable, true)
        val zoomEnabled = a.getBoolean(R.styleable.ZoomEngine_zoomEnabled, true)
        val flingEnabled = a.getBoolean(R.styleable.ZoomEngine_flingEnabled, true)
        val scrollEnabled = a.getBoolean(R.styleable.ZoomEngine_scrollEnabled, true)
        val oneFingerScrollEnabled = a.getBoolean(R.styleable.ZoomEngine_oneFingerScrollEnabled, true)
        val twoFingersScrollEnabled = a.getBoolean(R.styleable.ZoomEngine_twoFingersScrollEnabled, true)
        val threeFingersScrollEnabled = a.getBoolean(R.styleable.ZoomEngine_threeFingersScrollEnabled, true)
        val allowFlingInOverscroll = a.getBoolean(R.styleable.ZoomEngine_allowFlingInOverscroll, true)
        val hasChildren = a.getBoolean(R.styleable.ZoomEngine_hasClickableChildren, false)
        val minZoom = a.getFloat(R.styleable.ZoomEngine_minZoom, MIN_ZOOM_DEFAULT)
        val maxZoom = a.getFloat(R.styleable.ZoomEngine_maxZoom, MAX_ZOOM_DEFAULT)
        @ZoomType val minZoomMode = a.getInteger(R.styleable.ZoomEngine_minZoomType, MIN_ZOOM_DEFAULT_TYPE)
        @ZoomType val maxZoomMode = a.getInteger(R.styleable.ZoomEngine_maxZoomType, MAX_ZOOM_DEFAULT_TYPE)
        val transformation = a.getInteger(R.styleable.ZoomEngine_transformation, TRANSFORMATION_CENTER_INSIDE)
        val transformationGravity = a.getInt(R.styleable.ZoomEngine_transformationGravity, TRANSFORMATION_GRAVITY_AUTO)
        val alignment = a.getInt(R.styleable.ZoomEngine_alignment, ALIGNMENT_DEFAULT)
        val animationDuration = a.getInt(R.styleable.ZoomEngine_animationDuration, ZoomEngine.DEFAULT_ANIMATION_DURATION.toInt()).toLong()
        a.recycle()

        engine.setContainer(this)
        engine.addListener(object : ZoomEngine.Listener {
            override fun onIdle(engine: ZoomEngine) {}
            override fun onUpdate(engine: ZoomEngine, matrix: Matrix) {
                onUpdate()
            }
        })
        setTransformation(transformation, transformationGravity)
        setAlignment(alignment)
        setOverScrollHorizontal(overScrollHorizontal)
        setOverScrollVertical(overScrollVertical)
        setHorizontalPanEnabled(horizontalPanEnabled)
        setVerticalPanEnabled(verticalPanEnabled)
        setOverPinchable(overPinchable)
        setZoomEnabled(zoomEnabled)
        setFlingEnabled(flingEnabled)
        setScrollEnabled(scrollEnabled)
        setOneFingerScrollEnabled(oneFingerScrollEnabled)
        setTwoFingersScrollEnabled(twoFingersScrollEnabled)
        setThreeFingersScrollEnabled(threeFingersScrollEnabled)
        setAllowFlingInOverscroll(allowFlingInOverscroll)
        setAnimationDuration(animationDuration)
        setMinZoom(minZoom, minZoomMode)
        setMaxZoom(maxZoom, maxZoomMode)
        setHasClickableChildren(hasChildren)

        setWillNotDraw(false)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        engine.setContentSize(measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (childCount > 0) {
            throw RuntimeException("$TAG accepts only a single child.")
        }
        super.addView(child, index, params)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return engine.onInterceptTouchEvent(ev) || hasClickableChildren && super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return engine.onTouchEvent(ev) || hasClickableChildren && super.onTouchEvent(ev)
    }

    private fun onUpdate() {
        if (hasClickableChildren) {
            if (childCount > 0) {
                val child = getChildAt(0)
                child.pivotX = 0f
                child.pivotY = 0f
                child.translationX = engine.scaledPanX
                child.translationY = engine.scaledPanY
                child.scaleX = engine.realZoom
                child.scaleY = engine.realZoom
            }
        } else {
            invalidate()
        }
        if ((isHorizontalScrollBarEnabled || isVerticalScrollBarEnabled) && !awakenScrollBars()) {
            invalidate()
        }
    }

    override fun computeHorizontalScrollOffset(): Int = engine.computeHorizontalScrollOffset()

    override fun computeHorizontalScrollRange(): Int = engine.computeHorizontalScrollRange()

    override fun computeVerticalScrollOffset(): Int = engine.computeVerticalScrollOffset()

    override fun computeVerticalScrollRange(): Int = engine.computeVerticalScrollRange()

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        val result: Boolean

        if (!hasClickableChildren) {
            val save = canvas.save()
            canvas.concat(engine.matrix)
            result = super.drawChild(canvas, child, drawingTime)
            canvas.restoreToCount(save)
        } else {
            result = super.drawChild(canvas, child, drawingTime)
        }

        return result
    }

    //endregion

    //region APIs


    /**
     * Whether the view hierarchy inside has (or will have) clickable children.
     * This is false by default.
     *
     * @param hasClickableChildren whether we have clickable children
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun setHasClickableChildren(hasClickableChildren: Boolean) {
        if (this.hasClickableChildren && !hasClickableChildren) {
            // Revert any transformation that was applied to our child.
            if (childCount > 0) {
                val child = getChildAt(0)
                child.scaleX = 1f
                child.scaleY = 1f
                child.translationX = 0f
                child.translationY = 0f
            }
        }
        this.hasClickableChildren = hasClickableChildren

        // Update if we were laid out already.
        if (width > 0 && height > 0) {
            if (this.hasClickableChildren) {
                onUpdate()
            } else {
                invalidate()
            }
        }
    }

    //endregion

    companion object {
        private val TAG = ZoomLayout::class.java.simpleName
    }

}
