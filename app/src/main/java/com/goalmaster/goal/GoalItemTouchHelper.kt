package com.goalmaster.goal

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.goalmaster.R

class GoalItemTouchHelper(val fragment: Fragment, private val adapter: GoalListAdapter,
                          private val recyclerView: RecyclerView)
    : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT)  {

    private val background = ColorDrawable(fragment.resources.getColor(R.color.black_shadows, fragment.context?.theme))

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        recyclerView.post { adapter.showMenu(viewHolder.adapterPosition) }
    }


    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView: View = viewHolder.itemView
        if (dX > 0) {
            background.setBounds(itemView.left, itemView.top,
                itemView.left + dX.toInt(), itemView.bottom)
        } else if (dX < 0) {
            background.setBounds(itemView.right + dX.toInt(), itemView.top,
                itemView.right, itemView.bottom)
        } else {
            background.setBounds(0, 0, 0, 0)
        }
        background.draw(c)
    }
}