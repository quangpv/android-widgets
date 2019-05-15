package com.kantek.yelp.widgets

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.support.annotation.StringRes
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.kantek.yelp.R
import com.kantek.yelp.annotations.ActionBarOptions
import com.kantek.yelp.extensions.hide
import com.kantek.yelp.extensions.setContentView
import com.kantek.yelp.extensions.show
import com.kantek.yelp.functionals.OnKeyDoneClickListener
import kotlinx.android.synthetic.main.app_action_bar.view.*


class AppActionBar : ConstraintLayout {
    private var mTitle: String? = null
    private var mTextRight: String? = null
    private var mIsShowBack: Boolean = false

    private var mDefaultElevation: Float = 0f
    private var mDefaultTranslateZ: Float = 0f
    private var mDefaultTitleTextSize: Float = 0f

    private var mBackColor: Int = 0
    private var mIcon1Res: Int = 0
    private var mIcon2Res: Int = 0
    private var mIcon3Res: Int = 0

    private lateinit var mDefaultTitleTypeface: Typeface

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        setContentView(R.layout.app_action_bar)
        saveDefault()
        loadAttrs(attrs)
        setupViews()
    }

    private fun loadAttrs(attrs: AttributeSet?) {
        if (attrs == null) return
        val types = context.obtainStyledAttributes(attrs, R.styleable.AppActionBar)
        mTitle = types.getString(R.styleable.AppActionBar_title)
        mTextRight = types.getString(R.styleable.AppActionBar_textRight)
        mIsShowBack = types.getBoolean(R.styleable.AppActionBar_showBack, false)
        mBackColor = types.getColor(R.styleable.AppActionBar_backColor, 0)
        mIcon1Res = types.getResourceId(R.styleable.AppActionBar_icon1, 0)
        mIcon2Res = types.getResourceId(R.styleable.AppActionBar_icon2, 0)
        mIcon3Res = types.getResourceId(R.styleable.AppActionBar_icon3, 0)

        types.recycle()
    }

    private fun saveDefault() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDefaultElevation = elevation
            mDefaultTranslateZ = translationZ
        }
        mDefaultTitleTextSize = abTitle.textSize
        mDefaultTitleTypeface = abTitle.typeface
    }

    private fun setupViews() {
//        showElevation(true)
        showTitle(mTitle)
        showSearch(0)
        showBack(mIsShowBack)
        showTextRight(mTextRight)
        showIcon1(mIcon1Res)
        showIcon2(mIcon2Res)
        showIcon3(mIcon3Res)
        showBackColor(mBackColor)
        setBackgroundResource(R.color.colorPrimary)
        layoutTransition = LayoutTransition()
    }


    fun showTextRight(text: String?) {
        showText(abTextRight, text)
    }

    fun showTextRight(text: Int) {
        showText(abTextRight, text)
    }

    private fun showElevation(b: Boolean) {
        if (b) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                elevation = resources.getDimension(R.dimen.size_10)
                translationY = resources.getDimension(R.dimen.size_10)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                elevation = mDefaultElevation
                translationZ = mDefaultTranslateZ
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(
            resources.getDimensionPixelSize(R.dimen.size_50),
            MeasureSpec.EXACTLY
        ))
    }

    private fun showIcon(view: ImageView, resId: Int) {
        if (resId != 0) {
            view.visibility = View.VISIBLE
            view.setImageResource(resId)
        } else {
            view.visibility = View.GONE
        }
    }

    private fun showText(view: TextView, text: String?) {
        if (text != null && !text.isEmpty()) {
            view.visibility = View.VISIBLE
            view.text = text
        } else {
            view.visibility = View.GONE
        }
    }

    private fun showText(view: TextView, @StringRes text: Int) {
        if (text != 0) {
            view.visibility = View.VISIBLE
            view.setText(text)
        } else {
            view.visibility = View.GONE
        }
    }

    fun showIcon1(resId: Int) {
        showIcon(abIcon1, resId)
    }

    fun showSearch(@StringRes resId: Int) {
        if (resId != 0) {
            edtSearch.show()
            edtSearch.setHint(resId)
        } else edtSearch.hide()
    }

    fun showIcon2(resId: Int) {
        showIcon(abIcon2, resId)
    }

    fun showIcon3(resId: Int) {
        showIcon(abIcon3, resId)
    }

    fun showTitle(text: String?) {
        showText(abTitle, text)
    }

    fun showTitle(text: Int) {
        showText(abTitle, text)
    }

    private fun showBack(b: Boolean) {
        abBack.visibility = if (b) View.VISIBLE else View.GONE
    }

    fun setupWithOptions(actionBarOptions: ActionBarOptions) {
        show(actionBarOptions.visible)
        if (!actionBarOptions.visible) return
        showTitle(actionBarOptions.title)
        showBack(actionBarOptions.back)
        showBackColor(actionBarOptions.backColor)
        showTextRight(actionBarOptions.textRight)
        showIcon1(actionBarOptions.icon1)
        showIcon2(actionBarOptions.icon2)
        showIcon3(actionBarOptions.icon3)
        showSearch(actionBarOptions.search)
    }

    private fun showBackColor(backColor: Int) {
        abBack.setColorFilter(backColor)
    }

    private fun show(visible: Boolean) {
        if (visible) {
            if (!isShown) visibility = View.VISIBLE
        } else {
            if (isShown) visibility = View.GONE
        }
    }

    fun setOnBackClickListener(function: (View) -> Unit) {
        abBack.setOnClickListener(function)
    }

    fun setOnIcon1ClickListener(function: (View) -> Unit) {
        abIcon1.setOnClickListener(function)
    }

    fun setOnIcon2ClickListener(function: (View) -> Unit) {
        abIcon2.setOnClickListener(function)
    }

    fun setOnIcon3ClickListener(function: (View) -> Unit) {
        abIcon3.setOnClickListener(function)
    }

    fun setOnTextRightClickListener(function: (View) -> Unit) {
        abTextRight.setOnClickListener(function)
    }

    fun setOnSearchClickListener(function: () -> Unit) {
        edtSearch.setOnEditorActionListener(OnKeyDoneClickListener(function))
    }

}