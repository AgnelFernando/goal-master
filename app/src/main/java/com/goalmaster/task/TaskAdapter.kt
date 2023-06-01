package com.goalmaster.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goalmaster.databinding.TaskCompactViewBinding
import com.goalmaster.goal.view.viewgoal.GoalTasksCallbacks
import com.goalmaster.task.data.entity.Task

class TaskAdapter(val callbacks: GoalTasksCallbacks):
    ListAdapter<Task, RecyclerView.ViewHolder>(TASK_COMPARATOR) {


    init {
        setHasStableIds(true)
    }

    companion object {
        val TASK_COMPARATOR = object : DiffUtil.ItemCallback<Task>() {
            override fun areContentsTheSame(
                oldItem: Task,
                newItem: Task
            ): Boolean = oldItem.id == newItem.id

            override fun areItemsTheSame(
                oldItem: Task,
                newItem: Task
            ): Boolean = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = TaskCompactViewBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        (holder as TaskViewHolder).bind(data, callbacks)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

    class TaskViewHolder(item: View) : RecyclerView.ViewHolder(item) {

        private var binding: TaskCompactViewBinding? = null

        constructor(binding: TaskCompactViewBinding) : this(binding.root) {
            this.binding = binding
        }

        fun bind(data: Task, callbacks: GoalTasksCallbacks) {
            binding?.let {
                it.task = data
                it.showMoreDetails = false
                it.taskCompactViewLayout.setOnClickListener {
                    callbacks.openTask(data.id)
                }
                itemView.invalidate()
            }
        }
    }
}