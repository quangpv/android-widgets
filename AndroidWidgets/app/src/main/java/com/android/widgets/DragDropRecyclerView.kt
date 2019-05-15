package com.android.widgets

import android.content.Context
import android.graphics.Canvas
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.AttributeSet

class DragDropRecyclerView : RecyclerView {

    private var mTouchHelper: ItemTouchHelper? = null
    private var mEnableSwipe = true

    private val isVertical: Boolean
        get() {
            val layoutManager = layoutManager
            if (layoutManager is GridLayoutManager)
                return layoutManager.orientation == VERTICAL
            if (layoutManager is LinearLayoutManager)
                return layoutManager.orientation == VERTICAL
            throw RuntimeException("Currently, not support this layout manager")
        }

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        mTouchHelper = ItemTouchHelper(DragAndDropTouch())
        mTouchHelper!!.attachToRecyclerView(this)
        setHasFixedSize(true)
    }

    fun setEnableSwipe(enableSwipe: Boolean) {
        mEnableSwipe = enableSwipe
    }

    private fun getSwipeOrientation(viewHolder: ViewHolder): Int {
        if (viewHolder !is Draggable) return 0
        if (!viewHolder.shouldSwipe()) return 0

        if (layoutManager is GridLayoutManager) return 0
        val linearLayoutManager = layoutManager as LinearLayoutManager
        return if (linearLayoutManager.orientation == VERTICAL)
            ItemTouchHelper.START or ItemTouchHelper.END
        else ItemTouchHelper.UP or ItemTouchHelper.DOWN
    }

    private fun getDragOrientation(viewHolder: ViewHolder): Int {
        if (viewHolder !is Draggable) return 0
        if (layoutManager is GridLayoutManager) {
            return (ItemTouchHelper.UP
                    or ItemTouchHelper.DOWN
                    or ItemTouchHelper.LEFT
                    or ItemTouchHelper.RIGHT)
        }
        val linearLayoutManager = layoutManager as LinearLayoutManager
        return if (linearLayoutManager.orientation == VERTICAL)
            ItemTouchHelper.UP or ItemTouchHelper.DOWN
        else ItemTouchHelper.START or ItemTouchHelper.END
    }

    fun drag(holder: ViewHolder) {
        mTouchHelper!!.startDrag(holder)
    }

    inner class DragAndDropTouch : ItemTouchHelper.Callback() {
        private var mDeltaMoved: Float = 0f

        override fun isLongPressDragEnabled(): Boolean {
            return true
        }

        override fun isItemViewSwipeEnabled(): Boolean {
            return mEnableSwipe
        }

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
            val dragFlags = getDragOrientation(viewHolder)
            val swipeFlags = getSwipeOrientation(viewHolder)
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(recyclerView: RecyclerView, source: ViewHolder, target: ViewHolder): Boolean {
            if (source.itemViewType != target.itemViewType) return false
            if (source is Draggable) {
                (source as Draggable).onMoved(target.adapterPosition)
                return true
            }
            return false
        }

        override fun onSwiped(viewHolder: ViewHolder, i: Int) {
            if (viewHolder is Draggable) (viewHolder as Draggable).onDismiss()
        }

        override fun onChildDraw(c: Canvas,
                                 recyclerView: RecyclerView,
                                 viewHolder: ViewHolder,
                                 dX: Float, dY: Float,
                                 actionState: Int,
                                 isCurrentlyActive: Boolean) {
            if (viewHolder !is Draggable) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                return
            }
            when (actionState) {
                ItemTouchHelper.ACTION_STATE_SWIPE -> {
                    if (isVertical) {
                        viewHolder.itemView.translationX = dX
                        val alpha = ALPHA_FULL - Math.abs(dX) / viewHolder.itemView.width.toFloat()
                        viewHolder.itemView.alpha = alpha
                    } else {
                        viewHolder.itemView.translationY = dY
                        val alpha = ALPHA_FULL - Math.abs(dY) / viewHolder.itemView.height.toFloat()
                        viewHolder.itemView.alpha = alpha
                    }
                    return
                }
                ItemTouchHelper.ACTION_STATE_DRAG ->
                    mDeltaMoved += if (isVertical) Math.abs(dY) else Math.abs(dX)
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

        override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                if (viewHolder is Draggable) {
                    mDeltaMoved = 0f
                    viewHolder.onDragged()
                }
            }
            super.onSelectedChanged(viewHolder, actionState)
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            viewHolder.itemView.alpha = ALPHA_FULL
            if (viewHolder is Draggable) {
                if (mDeltaMoved <= CLICK_LIMITED) viewHolder.onDropImmediate()
                viewHolder.onDropped()
            }
        }
    }

    companion object {
        const val ALPHA_FULL = 1.0f
        const val CLICK_LIMITED = 10
    }
}

interface Draggable {
    fun onMoved(newPosition: Int)

    fun onDragged() {}

    fun onDropped() {}

    fun onDismiss() {}

    fun onDropImmediate() {}

    fun shouldSwipe() = false
}
