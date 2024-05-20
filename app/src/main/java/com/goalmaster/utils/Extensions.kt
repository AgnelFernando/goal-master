package com.goalmaster.utils

import android.content.Context
import android.text.format.DateUtils
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import com.goalmaster.R
import com.goalmaster.task.data.entity.TaskWithData
import java.util.*

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {

    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            observer.onChanged(value)
            removeObserver(this)
        }
    })
}

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