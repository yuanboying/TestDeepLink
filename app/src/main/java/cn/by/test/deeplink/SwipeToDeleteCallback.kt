package cn.by.test.deeplink

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import cn.by.test.R

abstract class SwipeToDeleteCallback(context: Context) : ItemTouchHelper.Callback() {

    //region Variables
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
    private val background = ColorDrawable()
    private val backgroundColor = ContextCompat.getColor(context, R.color.swipe_bg_color)
    private val deleteDrawable = ContextCompat.getDrawable(context, R.drawable.ic_delete)
    private val intrinsicWidth = deleteDrawable?.intrinsicWidth ?: 0
    private val intrinsicHeight = deleteDrawable?.intrinsicHeight ?: 0
    //endregion

    //region Implements
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false
    //endregion

    //region Overrides
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView.findViewById<CardView>(R.id.deepLinkCard)
        val isCancelled = dX == 0f && !isCurrentlyActive
        if (isCancelled) {
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
        } else {
            background.apply {
                color = backgroundColor
                setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                draw(c)
            }

            val deleteIconMargin = (itemView.height - intrinsicHeight) / 2
            val deleteIconTop = itemView.top + (itemView.height - intrinsicHeight) / 2
            val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
            val deleteIconRight = itemView.right - deleteIconMargin
            val deleteIconBottom = deleteIconTop + intrinsicHeight

            deleteDrawable?.apply {
                setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
                draw(c)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7f
    }
    //endregion

    //region Private Helpers
    private fun clearCanvas(
        c: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ) {
        c.drawRect(left, top, right, bottom, clearPaint)
    }
    //endregion
}