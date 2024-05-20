package com.goalmaster.plan.view.planner

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.goalmaster.utils.TASK_COMPARATOR
import com.goalmaster.databinding.PlannedTaskViewBinding
import com.goalmaster.task.data.entity.TaskWithData
import java.time.Duration
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.minutes

class PlanTaskTempAdapter(private val plannedTaskOptions: PlanTaskOptionImpl) :
    ListAdapter<TaskWithData, RecyclerView.ViewHolder>(TASK_COMPARATOR) {

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
                val task = data.task
                val id = task.id

                it.taskName = task.name
                it.goalName = data.goal.name
                it.eventTime = task.durationInMin?.minutes.toString()
                it.showOption = false
                it.status = data.planTask!!.status

                val totalTimeWorked = data.timeTracked.sumOf { ttt ->
                    val endTime = ttt.endTime ?: LocalDateTime.now()
                    Duration.between(ttt.startTime, endTime).toMinutes()
                }

                it.nowWorking = data.timeTracked.any { ttt -> ttt.endTime == null }

                it.totalTimeWorked = totalTimeWorked.minutes.toString()


                it.deleteButton.setOnClickListener {
                    options.onDeleteClicked(id)
                }

                it.viewButton.setOnClickListener {
                    options.onViewClicked(id)
                }

                it.addEventButton.setOnClickListener {
                    options.onAddEventClicked(id)
                }

                it.doneButton.setOnClickListener {
                    options.onDoneClicked(id)
                }

                itemView.invalidate()
            }
        }
    }
}