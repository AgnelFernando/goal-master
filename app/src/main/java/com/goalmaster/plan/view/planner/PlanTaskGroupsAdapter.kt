//package com.goalmaster.plan.view.planner
//
//import android.annotation.SuppressLint
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.goalmaster.GoalOptionImpl
//import com.goalmaster.R
//import com.goalmaster.databinding.GoalCompactViewBinding
//import com.goalmaster.goal.data.entity.GoalData
//import com.goalmaster.relativeDateFormat
//import com.goalmaster.task.data.entity.Task
//
//class PlanTaskGroupsAdapter:
//    ListAdapter<Task, RecyclerView.ViewHolder>(PLAN_TASK_GROUP_COMPARATOR) {
//
//
//        private val SHOW_MENU = 1
//        private val HIDE_MENU = 2
//
//        init {
//            setHasStableIds(true)
//        }
//
//        companion object {
//            val PLAN_TASK_GROUP_COMPARATOR = object : DiffUtil.ItemCallback<Task>() {
//                override fun areContentsTheSame(
//                    oldItem: Task,
//                    newItem: Task
//                ): Boolean = oldItem.id == newItem.id
//
//                override fun areItemsTheSame(
//                    oldItem: Task,
//                    newItem: Task
//                ): Boolean = oldItem == newItem
//            }
//        }
//
//
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//            return if (viewType == SHOW_MENU) {
//                val v = LayoutInflater.from(parent.context).inflate(R.layout.plan_task_group, parent, false)
//                MenuViewHolder(v)
//            } else {
//                val binding = GoalCompactViewBinding
//                    .inflate(LayoutInflater.from(parent.context), parent, false)
//                GoalViewHolder(binding)
//            }
//        }
//
//        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//            val data = getItem(position)
//            if (holder is GoalViewHolder) {
//                holder.bind(data)
//            } else {
//                holder as MenuViewHolder
//                val optionEdit = holder.itemView.findViewById<View>(R.id.goalOptionEdit)
//                val optionDelete = holder.itemView.findViewById<View>(R.id.goalOptionDelete)
//                val optionClose = holder.itemView.findViewById<View>(R.id.goalOptionClose)
//
//                optionEdit.setOnClickListener {
//                    closeMenu()
//                    goalOptions.onEditClicked(data.id)
//                }
//
//                optionDelete.setOnClickListener {
//                    closeMenu()
//                    goalOptions.onDeleteClicked(data.id)
//                }
//
//                optionClose.setOnClickListener {
//                    closeMenu()
//                }
//            }
//        }
//
//        override fun getItemId(position: Int): Long {
//            return getItem(position).id
//        }
//
//        @SuppressLint("NotifyDataSetChanged")
//        fun showMenu(position: Int) {
//            if (getItem(position).showMenu) {
//                getItem(position).showMenu = false
//                notifyDataSetChanged()
//                return
//            }
//
//            for (i in 0 until itemCount) {
//                getItem(i).showMenu = false
//            }
//            getItem(position).showMenu = true
//            notifyDataSetChanged()
//        }
//
//        fun isMenuShown(): Boolean {
//            for (i in 0 until itemCount) {
//                if (getItem(i).showMenu) {
//                    return true
//                }
//            }
//            return false
//        }
//
//        @SuppressLint("NotifyDataSetChanged")
//        fun closeMenu() {
//            for (i in 0 until itemCount) {
//                getItem(i).showMenu = false
//            }
//            notifyDataSetChanged()
//        }
//
//        class GoalViewHolder(item: View) : RecyclerView.ViewHolder(item) {
//
//            private var binding: GoalCompactViewBinding? = null
//
//            constructor(binding: GoalCompactViewBinding) : this(binding.root) {
//                this.binding = binding
//            }
//
//            @SuppressLint("SetTextI18n")
//            fun bind(data: GoalData, goalOptions: GoalOptionImpl) {
//                binding?.let {
//                    it.goal = data
//                    it.goalLastUpdated.text = data.updatedOn.relativeDateFormat(itemView.context)
//                    val progress = (data.completed.toDouble() / data.total.toDouble()) * 100
//                    it.progressPercentage = progress.toInt()
//                    it.root.setOnClickListener {
//                        goalOptions.openUpdateProgressDialog(data.id)
//                    }
//                    itemView.invalidate()
//                }
//            }
//        }
//
//        class MenuViewHolder(item: View) : RecyclerView.ViewHolder(item)
//    }
