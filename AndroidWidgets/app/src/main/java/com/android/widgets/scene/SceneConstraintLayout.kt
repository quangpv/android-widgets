package com.android.widgets.scene

import android.content.Context
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.transition.ChangeBounds
import android.transition.Transition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.animation.DecelerateInterpolator

class SceneConstraintLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), SceneLayout {

    private var mSetCache = hashMapOf<Int, ConstraintSet>()
    override fun onFinishInflate() {
        super.onFinishInflate()
        mSetCache[0] = ConstraintSet().apply { clone(this@SceneConstraintLayout) }
    }

    var currentLayout: Int = 0
        private set

    override fun isOriginalScene() = currentLayout == 0

    override fun goScene(layout: Int, transitionManager: Int) {
        currentLayout = layout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(this, ChangeBounds().apply {
                interpolator = DecelerateInterpolator()
            })
        }
        getConstraintSet(layout).applyTo(this@SceneConstraintLayout)
    }

    override fun goScene(layout: Int, transition: Transition) {
        currentLayout = layout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(this, transition)
        }
        getConstraintSet(layout).applyTo(this@SceneConstraintLayout)
    }

    private fun getConstraintSet(layout: Int): ConstraintSet {
        if (!mSetCache.containsKey(layout))
            mSetCache[layout] = ConstraintSet().apply { clone(context, layout) }
        return mSetCache[layout]!!
    }
}