package com.android.widgets.zoom

internal abstract class MovementManager(private val controllerProvider: () -> MatrixController) {

    protected val controller get() = controllerProvider()

    /**
     * Whether this movement is enabled.
     */
    abstract val isEnabled: Boolean

    /**
     * Whether over-movement is allowed, which means,
     * temporarily going over the boundaries during a gesture.
     */
    abstract val isOverEnabled: Boolean

    /**
     * Resets to the initial state.
     * Should not clear settings (like min/max values and options),
     * but just the current state if any.
     */
    abstract fun clear()
}
