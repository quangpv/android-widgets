package com.android.widgets.scene

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.annotation.TransitionRes
import android.transition.Scene
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.android.widgets.scene.SceneLayout

class SceneRelativeLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), SceneLayout {

    private lateinit var mRootChild: View
    private lateinit var mRootScene: Scene
    var currentLayout: Int = 0
        private set

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onFinishInflate() {
        super.onFinishInflate()
        mRootChild = getChildAt(0)
        mRootScene = Scene(this, mRootChild as ViewGroup)
    }

    override fun isOriginalScene() = currentLayout == 0

    override fun goScene(layout: Int, transitionManager: Int) {
        doGo(layout) {
            if (transitionManager == 0) TransitionManager.go(it)
            else loadTranManager(transitionManager)?.transitionTo(it)
        }
    }

    private fun doGo(layout: Int, function: (Scene) -> Unit) {
        currentLayout = layout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val scene = if (layout == 0) mRootScene else Scene.getSceneForLayout(this, layout, context)
            function(scene)
        } else {
            if (layout != 0) setContentView(layout) else setContentView(mRootChild)
        }
    }

    override fun goScene(layout: Int, transition: Transition) {
        doGo(layout) {
            TransitionManager.go(it, transition)
        }
    }

    private fun setContentView(layout: Int) {
        LayoutInflater.from(context).inflate(layout, this, true)
    }

    private fun setContentView(layout: View) {
        removeAllViews()
        addView(layout)
    }

    private fun loadTranManager(@TransitionRes transitionManager: Int): TransitionManager? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            var tranManager = getTag(transitionManager) as? TransitionManager
            if (tranManager == null) {
                tranManager = TransitionInflater.from(context).inflateTransitionManager(transitionManager, this)
                setTag(transitionManager, tranManager)
            }
            tranManager!!
        } else null
    }
}