package com.android.widgets.zoom


internal class ZoomManager(provider: () -> MatrixController) : MovementManager(provider) {

    internal var transformationZoom = 0F

    private var minZoom = MIN_ZOOM_DEFAULT
    private var minZoomMode = MIN_ZOOM_DEFAULT_TYPE
    private var maxZoom = MAX_ZOOM_DEFAULT
    private var maxZoomMode = MAX_ZOOM_DEFAULT_TYPE

    override var isEnabled = true
    override var isOverEnabled = true

    /**
     * Clears the current variable state, that is,
     * resets [transformationZoom].
     */
    override fun clear() {
        transformationZoom = 0F
    }

    /**
     * Transforms a [RealZoom] into a [Zoom].
     */
    @Zoom
    internal fun realZoomToZoom(@RealZoom realZoom: Float): Float {
        return realZoom / transformationZoom
    }

    /**
     * Transforms a [Zoom] into a [RealZoom].
     */
    @RealZoom
    internal fun zoomToRealZoom(@Zoom zoom: Float): Float {
        return zoom * transformationZoom
    }

    /**
     * Sets the maximum zoom and type allowed.
     */
    internal fun setMaxZoom(maxZoom: Float, @ZoomType type: Int) {
        if (maxZoom < 0) {
            throw IllegalArgumentException("Max zoom should be >= 0.")
        }
        this.maxZoom = maxZoom
        this.maxZoomMode = type
    }

    /**
     * Sets the minimum zoom and type allowed.
     */
    internal fun setMinZoom(minZoom: Float, @ZoomType type: Int) {
        if (minZoom < 0) {
            throw IllegalArgumentException("Min zoom should be >= 0")
        }
        this.minZoom = minZoom
        this.minZoomMode = type
    }

    /**
     * The amount of overzoom that is allowed in both directions. This is currently
     * a fixed value, but might be made configurable in the future.
     */
    @RealZoom
    internal val maxOverZoom: Float
        get() = DEFAULT_OVERZOOM_FACTOR * (getMaxZoom() - getMinZoom())

    /**
     * Returns the current minimum zoom as a [RealZoom] value.
     */
    @RealZoom
    internal fun getMinZoom(): Float {
        return when (minZoomMode) {
            TYPE_REAL_ZOOM -> minZoom
            TYPE_ZOOM -> zoomToRealZoom(minZoom)
            else -> throw IllegalArgumentException("Unknown ZoomType $minZoomMode")
        }
    }

    /**
     * Returns the current maximum zoom as a [RealZoom] value.
     */
    @RealZoom
    internal fun getMaxZoom(): Float {
        return when (maxZoomMode) {
            TYPE_REAL_ZOOM -> maxZoom
            TYPE_ZOOM -> zoomToRealZoom(maxZoom)
            else -> throw IllegalArgumentException("Unknown ZoomType $maxZoomMode")
        }
    }

    /**
     * Checks if the passed in zoom level is in expected bounds.
     *
     * @param value the zoom level to check
     * @param allowOverZoom set to true if zoom values within overpinch range should be considered valid
     * @return the zoom level that will lead into a valid state when applied.
     */
    @RealZoom
    internal fun checkBounds(@RealZoom value: Float, allowOverZoom: Boolean): Float {
        var minZoom = getMinZoom()
        var maxZoom = getMaxZoom()
        if (allowOverZoom && isOverEnabled) {
            minZoom -= maxOverZoom
            maxZoom += maxOverZoom
        }
        return value.coerceIn(minZoom, maxZoom)
    }

    companion object {
        private const val DEFAULT_OVERZOOM_FACTOR = 0.1f
    }
}
