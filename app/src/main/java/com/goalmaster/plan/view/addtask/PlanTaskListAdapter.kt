package com.goalmaster.plan.view.addtask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goalmaster.databinding.PlanTaskListBinding
import com.goalmaster.task.data.entity.TaskWithData
import kotlin.time.Duration.Companion.minutes

class PlanTaskListAdapter : ListAdapter<TaskWithData, RecyclerView.ViewHolder>(TASK_COMPARATOR) {

    var selectedPos = RecyclerView.NO_POSITION

    init {
        setHasStableIds(true)

    }

    companion object {
        val TASK_COMPARATOR = object : DiffUtil.ItemCallback<TaskWithData>() {
            override fun areContentsTheSame(
                oldItem: TaskWithData,
                newItem: TaskWithData
            ): Boolean = oldItem.task.id == newItem.task.id

            override fun areItemsTheSame(
                oldItem: TaskWithData,
                newItem: TaskWithData
            ): Boolean = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = PlanTaskListBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as TaskViewHolder
        val data = getItem(position)
        holder.binding?.isSelected = selectedPos == position

        holder.binding?.root?.setOnClickListener {
            notifyItemChanged(selectedPos);
            selectedPos = position
            notifyItemChanged(selectedPos);
        }

        holder.bind(data)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).task.id
    }

    fun getSelectedTask(): Long {
        return getItemId(selectedPos)
    }


    class TaskViewHolder(item: View) : RecyclerView.ViewHolder(item) {

        var binding: PlanTaskListBinding? = null

        constructor(binding: PlanTaskListBinding) : this(binding.root) {
            this.binding = binding
        }

        fun bind(data: TaskWithData) {
            binding?.let {
                it.task = data.task
                it.goal = data.goal
                it.duration = data.task.durationInMin?.minutes?.toString()
                itemView.invalidate()
            }
        }
    }
}