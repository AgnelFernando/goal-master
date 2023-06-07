package com.goalmaster.plan.view.planner

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goalmaster.databinding.PlannedTaskViewBinding
import com.goalmaster.task.data.entity.TaskWithData
import kotlin.time.Duration.Companion.minutes

class PlanTaskTempAdapter(private val plannedTaskOptions: PlanTaskOptionImpl) :
    ListAdapter<TaskWithData, RecyclerView.ViewHolder>(TASK_COMPARATOR) {

    companion object {
        val TASK_COMPARATOR = object : DiffUtil.ItemCallback<TaskWithData>() {
            override fun areContentsTheSame(
                oldItem: TaskWithData,
                newItem: TaskWithData
            ): Boolean {
                if (newItem.planTask == null || oldItem.planTask == null) return false
                return oldItem.planTask.taskId == newItem.planTask.taskId
                        && oldItem.planTask.planId == newItem.planTask.planId
            }

            override fun areItemsTheSame(
                oldItem: TaskWithData,
                newItem: TaskWithData
            ): Boolean = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = PlannedTaskViewBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanTaskTempViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        if (holder is PlanTaskTempViewHolder) {
            holder.bind(data, plannedTaskOptions)
        }
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).task.id
    }

    class PlanTaskTempViewHolder(item: View) : RecyclerView.ViewHolder(item) {

        private var binding: PlannedTaskViewBinding? = null

        constructor(binding: PlannedTaskViewBinding) : this(binding.root) {
            this.binding = binding
            this.binding!!.taskDetails.setOnClickListener {
                if (this.binding == null) return@setOnClickListener
                this.binding?.showOption = !this.binding!!.showOption!!
                it.invalidate()
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(data: TaskWithData, options: PlanTaskOptionImpl) {
            binding?.let {
                it.taskName = data.task.name
                it.goalName = data.goal.name
                it.eventTime = data.task.durationInMin?.minutes.toString()
                it.showOption = false
                it.deleteButton.setOnClickListener {
                    options.onDeleteClicked(data.task.id)
                }

                it.viewButton.setOnClickListener {
                    options.onViewClicked(data.task.id)
                }
                itemView.invalidate()
            }
        }
    }
}