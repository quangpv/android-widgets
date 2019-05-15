package com.android.widgets.zoom


import android.support.annotation.IntDef
import android.util.Log
import android.view.MotionEvent

internal class StateController(private val callback: Callback) {

    /**
     * Receives callbacks from this controller.
     */
    internal interface Callback {
        // State callbacks
        fun isStateAllowed(@State newState: Int): Boolean

        fun onStateIdle()
        fun cleanupState(@State oldState: Int)

        // Touch callbacks
        fun maybeStartPinchGesture(event: MotionEvent): Boolean

        fun maybeStartScrollFlingGesture(event: MotionEvent): Boolean
        fun endScrollGesture()
    }

    /**
     * Returns the current state.
     * One of the [State] constants.
     */
    @State
    internal var state: Int = IDLE
        private set


    /**
     * Whether this state needs cleanup even if called twice.
     * The [ANIMATING] state currently needs it.
     */
    private fun needsCleanupWhenCalledTwice(@State state: Int): Boolean {
        return state == ANIMATING
    }

    /**
     * Private function to set the current state.
     * External callers should use [setPinching], [setScrolling], [makeIdle]... instead.
     */
    private fun setState(@State newState: Int): Boolean {
        Log.v("trySetState:", newState.toStateName())
        if (!callback.isStateAllowed(newState)) return false
        if (newState == state && !needsCleanupWhenCalledTwice(newState)) return true
        val oldState = state

        when (newState) {
            SCROLLING -> if (oldState == PINCHING || oldState == ANIMATING) return false
            FLINGING -> if (oldState == ANIMATING) return false
            PINCHING -> if (oldState == ANIMATING) return false
            IDLE -> callback.onStateIdle()
        }

        // Now that it succeeded, do some cleanup.
        callback.cleanupState(oldState)
        Log.i("setState:", newState.toStateName())
        state = newState
        return true
    }

    /**
     * Processes the event. Should be called during the
     * [android.view.ViewGroup.onInterceptTouchEvent] callback.
     */
    internal fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return processTouchEvent(ev) > TOUCH_LISTEN
    }

    /**
     * Processes the event. Should be called during the
     * [android.view.View.onTouchEvent] callback.
     */
    internal fun onTouchEvent(ev: MotionEvent): Boolean {
        return processTouchEvent(ev) > TOUCH_NO
    }

    /**
     * Processes the touch event and returns one of [TOUCH_LISTEN],
     * [TOUCH_STEAL] or [TOUCH_NO].
     */
    private fun processTouchEvent(event: MotionEvent): Int {
        Log.v("processTouchEvent:", "start.")
        if (isAnimating()) return TOUCH_STEAL

        var result = callback.maybeStartPinchGesture(event)
        Log.v("processTouchEvent:", "scaleResult:$result")

        // Pinch detector always returns true. If we actually started a pinch,
        // Don't pass to fling detector.
        if (!isPinching()) {
            result = result or callback.maybeStartScrollFlingGesture(event)
            Log.v("processTouchEvent:", "flingResult:$result")
        }

        // Detect scroll ends, this appears to be the only way.
        if (isScrolling()) {
            val a = event.actionMasked
            if (a == MotionEvent.ACTION_UP || a == MotionEvent.ACTION_CANCEL) {
                Log.i("processTouchEvent:", "up event while scrolling, dispatching endScrollGesture.")
                // We are not simply calling makeIdle() because we might be in overpan.
                // In that case, the Callback will animate back to legit position.
                callback.endScrollGesture()
            }
        }

        return if (result && !isIdle()) {
            Log.v("processTouchEvent:", "returning: TOUCH_STEAL")
            TOUCH_STEAL
        } else if (result) {
            Log.v("processTouchEvent:", "returning: TOUCH_LISTEN")
            TOUCH_LISTEN
        } else {
            Log.v("processTouchEvent:", "returning: TOUCH_NO")
            makeIdle()
            TOUCH_NO
        }
    }

    /** Whether we are in [FLINGING] state. */
    internal fun isFlinging() = state == FLINGING

    /** Whether we are in [SCROLLING] state. */
    internal fun isScrolling() = state == SCROLLING

    /** Whether we are in [PINCHING] state. */
    internal fun isPinching() = state == PINCHING

    /** Whether we are in [ANIMATING] state. */
    internal fun isAnimating() = state == ANIMATING

    /** Whether we are in [IDLE] state. */
    internal fun isIdle() = state == IDLE

    /**
     * Moves state to [FLINGING]. Returns true if successful.
     */
    internal fun setFlinging() = setState(FLINGING)

    /**
     * Moves state to [SCROLLING]. Returns true if successful.
     */
    internal fun setScrolling() = setState(SCROLLING)

    /**
     * Moves state to [PINCHING]. Returns true if successful.
     */
    internal fun setPinching() = setState(PINCHING)

    /**
     * Moves state to [ANIMATING]. Returns true if successful.
     */
    internal fun setAnimating() = setState(ANIMATING)

    /**
     * Moves state to [IDLE]. Returns true if successful.
     */
    internal fun makeIdle() = setState(IDLE)

    private fun Int.toStateName(): String {
        return when (this) {
            IDLE -> "IDLE"
            FLINGING -> "FLINGING"
            SCROLLING -> "SCROLLING"
            PINCHING -> "PINCHING"
            ANIMATING -> "ANIMATING"
            else -> ""
        }
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(IDLE, SCROLLING, PINCHING, ANIMATING, FLINGING)
    internal annotation class State

    companion object {
        internal const val IDLE = 0
        internal const val SCROLLING = 1
        internal const val PINCHING = 2
        internal const val ANIMATING = 3
        internal const val FLINGING = 4

        /**
         * Constant for [processTouchEvent].
         * Indicates that we are not interested in this event stream.
         */
        private const val TOUCH_NO = 0

        /**
         * Constant for [processTouchEvent].
         * Indicates that we are interested in this event stream,
         * but we're not sure we have something just yet.
         * The gesture might start at a later point.
         */
        private const val TOUCH_LISTEN = 1

        /**
         * Constant for [processTouchEvent].
         * Indicates that we want to own this stream and intercept
         * it as long as this value is returned.
         */
        private const val TOUCH_STEAL = 2
    }
}