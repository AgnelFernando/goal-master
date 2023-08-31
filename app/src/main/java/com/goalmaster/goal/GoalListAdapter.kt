package com.goalmaster.goal

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goalmaster.GoalOptionImpl
import com.goalmaster.R
import com.goalmaster.databinding.GoalCompactViewBinding
import com.goalmaster.goal.data.entity.GoalData
import com.goalmaster.relativeDateFormat


class GoalListAdapter(private val goalOptions: GoalOptionImpl) :
    ListAdapter<GoalData, RecyclerView.ViewHolder>(GOAL_COMPARATOR) {


    private val SHOW_MENU = 1
    private val HIDE_MENU = 2

    init {
        setHasStableIds(true)
    }

    companion object {
        val GOAL_COMPARATOR = object : DiffUtil.ItemCallback<GoalData>() {
            override fun areContentsTheSame(
                oldItem: GoalData,
                newItem: GoalData
            ): Boolean = oldItem.id == newItem.id

            override fun areItemsTheSame(
                oldItem: GoalData,
                newItem: GoalData
            ): Boolean = oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).showMenu) {
            SHOW_MENU
        } else {
            HIDE_MENU
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SHOW_MENU) {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.goal_list_menu, parent, false)
            MenuViewHolder(v)
        } else {
            val binding = GoalCompactViewBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            GoalViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        if (holder is GoalViewHolder) {
            holder.bind(data, goalOptions)
        } else {
            holder as MenuViewHolder
            val optionEdit = holder.itemView.findViewById<View>(R.id.goalOptionEdit)
            val optionDelete = holder.itemView.findViewById<View>(R.id.goalOptionDelete)
            val optionClose = holder.itemView.findViewById<View>(R.id.goalOptionClose)

            optionEdit.setOnClickListener {
                closeMenu()
                goalOptions.onEditClicked(data.id)
            }

            optionDelete.setOnClickListener {
                closeMenu()
                goalOptions.onDeleteClicked(data.id)
            }

            optionClose.setOnClickListener {
                closeMenu()
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

//    @SuppressLint("NotifyDataSetChanged")
    fun showMenu(position: Int) {
//        if (getItem(position).showMenu) {
//            getItem(position).showMenu = false
//            notifyDataSetChanged()
//            return
//        }
//
//        for (i in 0 until itemCount) {
//            getItem(i).showMenu = false
//        }
//        getItem(position).showMenu = true
//        notifyDataSetChanged()
    }

    fun isMenuShown(): Boolean {
        for (i in 0 until itemCount) {
            if (getItem(i).showMenu) {
                return true
            }
        }
        return false
    }

//    @SuppressLint("NotifyDataSetChanged")
    fun closeMenu() {
//        for (i in 0 until itemCount) {
//            getItem(i).showMenu = false
//        }
//        notifyDataSetChanged()
    }

    class GoalViewHolder(item: View) : RecyclerView.ViewHolder(item) {

        private var binding: GoalCompactViewBinding? = null

        constructor(binding: GoalCompactViewBinding) : this(binding.root) {
            this.binding = binding
        }

        @SuppressLint("SetTextI18n")
        fun bind(data: GoalData, goalOptions: GoalOptionImpl) {
            binding?.let {
                it.goal = data
                val progress = (data.completedUnits.toDouble() / data.totalUnits.toDouble()) * 100
                it.progressPercentage = progress.toInt()
                it.root.setOnClickListener {
                    goalOptions.openUpdateProgressDialog(data.id)
                }
                itemView.invalidate()
            }
        }
    }

    class MenuViewHolder(item: View) : RecyclerView.ViewHolder(item)
}