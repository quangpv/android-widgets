package com.android.widgets.scene

import android.os.Build
import android.support.annotation.LayoutRes
import android.support.annotation.TransitionRes
import android.transition.Transition
import android.transition.TransitionManager
import android.view.ViewGroup

interface SceneLayout {
    fun goOriginalScene() = goScene()

    fun goCustomScene(function: () -> Unit) {
        function()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (this !is ViewGroup) throw RuntimeException("This is not ViewGroup")
            TransitionManager.beginDelayedTransition(this as ViewGroup)
        }
    }

    fun isOriginalScene(): Boolean

    fun goScene(@LayoutRes layout: Int = 0, @TransitionRes transitionManager: Int = 0)

    fun goScene(@LayoutRes layout: Int = 0, transition: Transition)
}