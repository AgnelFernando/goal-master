package com.goalmaster.plan.view.addtask

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goalmaster.Result
import com.goalmaster.plan.data.entity.Plan
import com.goalmaster.plan.data.entity.PlanTask
import com.goalmaster.plan.data.source.PlanRepository
import com.goalmaster.task.data.source.TaskRepository
import com.goalmaster.task.data.entity.TaskWithData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class TaskPlannerViewModel @Inject constructor(private val repository: TaskRepository,
                                               private val planRepository: PlanRepository,
                                               private val taskRepository: TaskRepository
) : ViewModel() {

    var taskId = 0L

    lateinit var plan: Plan

    var planTask = MutableLiveData<PlanTask>()

    val createPlanTaskEvent = MutableLiveData<Unit>()
    val placeHolderCreateEvent = MutableLiveData<Unit>()

    @ExperimentalCoroutinesApi
    val allTasks: Flow<List<TaskWithData>> = repository.observeTasksForPlan().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksForPlan = allTasks.transformLatest { td ->
        emit(td.filter { it.task.durationInMin != null }.sortedBy { it.goal.dueDate })
    }.asLiveData(viewModelScope.coroutineContext)


    fun setup() {
        viewModelScope.launch {
            val result = planRepository.getCurrentPlan()
            if (result is Result.Success) {
                plan = result.data
            }
        }
    }

    fun savePlanTask() {
        planTask
    }

    fun updateEventId(eventId: Long) {
        planTask.value ?: return
        planTask.value!!.eventId = eventId
    }

    fun createTaskPlan(taskId: Long): TaskWithData {
        val taskWithData = tasksForPlan.value!!.find { it.task.id == taskId }!!
        val data = PlanTask(plan.id, taskId, plan.startDate,
            taskWithData.task.durationInMin!!)
//        viewModelScope.launch {
//            val result = planRepository.savePlanTask(data)
//            if (result is Result.Success) {
                planTask.value = data
//            }
//        }

        return taskWithData
    }

    fun getTaskEventStartTime(): Long {
        return planTask.value!!.eventTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
    }

    fun getTaskEventEndTime(): Long {
        val planTaskValue = planTask.value!!
        return planTaskValue.eventTime.plusMinutes(planTaskValue.durationInMinutes.toLong())
            .atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
    }

    fun updateEventTime() {

    }

    fun saveSelectedPlanTask() {
        val data = planTask.value ?: return
        viewModelScope.launch {
            val result = planRepository.savePlanTask(data)
            if (result is Result.Success) {
                createPlanTaskEvent.value = Unit
            }
        }
    }

}