package com.goalmaster.review

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goalmaster.utils.TASK_COMPARATOR
import com.goalmaster.databinding.PlannedTaskReviewBinding
import com.goalmaster.plan.view.planner.PlanTaskOptionImpl
import com.goalmaster.task.data.entity.TaskWithData
import kotlin.time.Duration.Companion.minutes

class TaskReviewAdapter(private val plannedTaskOptions: PlanTaskOptionImpl) :
    ListAdapter<TaskWithData, RecyclerView.ViewHolder>(TASK_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = PlannedTaskReviewBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanTaskReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        if (holder is PlanTaskReviewViewHolder) {
            holder.bind(data, plannedTaskOptions)
        }
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).task.id
    }

    class PlanTaskReviewViewHolder(item: View) : RecyclerView.ViewHolder(item) {

        private var binding: PlannedTaskReviewBinding? = null

        constructor(binding: PlannedTaskReviewBinding) : this(binding.root) {
            this.binding = binding
        }

        @SuppressLint("SetTextI18n")
        fun bind(data: TaskWithData, options: PlanTaskOptionImpl) {
            binding?.let {
                it.taskName = data.task.name
                it.goalName = data.goal.name
                it.eventTime = data.task.durationInMin?.minutes.toString()
                it.doneButton.setOnClickListener {
                    options.onDoneClicked(data.task.id)
                }

                it.moveButton.setOnClickListener {
                    options.onMoveToNextDayClicked(data.task.id)
                }
                itemView.invalidate()
            }
        }
    }
}