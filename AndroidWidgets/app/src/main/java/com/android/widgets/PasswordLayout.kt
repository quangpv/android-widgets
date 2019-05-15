package com.android.widgets

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout

class PasswordLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var mIconColor: Int = Color.BLACK
    private lateinit var mIcon: ImageButton
    private val mIconPadding = resources.getDimensionPixelSize(R.dimen.size_5)

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        context.loadAttrs(attrs, R.styleable.PasswordLayout) {
            mIconColor = it.getColor(R.styleable.PasswordLayout_iconColor, Color.BLACK)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        fixLayoutOfChild()
        addToggleView()
    }

    private fun fixLayoutOfChild() {
        (getEditText().layoutParams as LayoutParams).apply {
            width = 0
            weight = 1f
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mIcon.setSize(measuredHeight, measuredHeight)
    }

    private fun addToggleView() {
        mIcon = ImageButton(context).also { view ->
            view.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            view.scaleType = ImageView.ScaleType.CENTER_INSIDE
            view.setImageResource(R.drawable.activator_password)
            view.setBackgroundResource(context.getAppResourceId(R.attr.selectableItemBackgroundBorderless))
            view.setPaddingAll(mIconPadding)
            view.setColorFilter(mIconColor)
            view.setOnClickListener {
                isActivated = !isActivated
                getEditText()
                    .showPassword(isActivated)
                    .seekCursorToLast()
            }
        }
        addView(mIcon)
    }

    private fun getEditText() = getChildAt(0) as EditText
}

private fun ImageButton.setPaddingAll(padding: Int) {
    setPadding(padding, padding, padding, padding)
}

private fun ImageButton.setSize(w: Int, h: Int) {
    layoutParams.apply {
        width = w
        height = h
    }
}

private fun EditText.seekCursorToLast() {
    setSelection(length())
}

private fun EditText.showPassword(activated: Boolean): EditText {
    transformationMethod = if (!activated) PasswordTransformationMethod.getInstance()
    else HideReturnsTransformationMethod.getInstance()
    return this
}


fun Context.getAppResourceId(attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.resourceId
}

fun Context.loadAttrs(attrs: AttributeSet?, attrType: IntArray, function: (TypedArray) -> Unit) {
    if (attrs == null) return
    val a = obtainStyledAttributes(attrs, attrType)
    function(a)
    a.recycle()
}
