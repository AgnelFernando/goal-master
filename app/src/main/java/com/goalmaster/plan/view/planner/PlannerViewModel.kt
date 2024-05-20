package com.goalmaster.plan.view.planner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goalmaster.plan.data.entity.Plan
import com.goalmaster.plan.data.entity.PlanState
import com.goalmaster.plan.data.entity.PlanTaskStatus
import com.goalmaster.plan.data.source.PlanRepository
import com.goalmaster.task.data.source.TaskRepository
import com.goalmaster.task.data.entity.TaskState
import com.goalmaster.task.data.entity.TaskWithData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val planRepository: PlanRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    val showPlanAndReviewBanner = MutableLiveData(false)

    val selectedDate: MutableStateFlow<LocalDate?> = MutableStateFlow(null)

    @ExperimentalCoroutinesApi
    val planFlow: Flow<Plan?> = planRepository.observeCurrentPlan().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val allPlannedTaskFlow = planRepository.observeCurrentPlanAllTasks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val plannedTaskFlow = allPlannedTaskFlow.transformLatest {
        emit(it.filter { t -> t.planTask?.status == PlanTaskStatus.OPENED }) }

    val allTasks = plannedTaskFlow.asLiveData(viewModelScope.coroutineContext)

    val plannedTask = allPlannedTaskFlow.combine(selectedDate) { l, d ->
        d ?: return@combine l
        l.filter { it.planTask!!.eventTime.toLocalDate().isEqual(d) }
    }.asLiveData(viewModelScope.coroutineContext)

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalHours = plannedTaskFlow.transformLatest { tasks ->
        var total = 0L
        tasks.forEach {
            val min = it.task.durationInMin ?: return@forEach
            total += min
        }
        emit("${total / 60}h ${total % 60}m")
    }.asLiveData(viewModelScope.coroutineContext)

    @OptIn(ExperimentalCoroutinesApi::class)
    val noPlan = planFlow.transformLatest {
        emit(it === null)
    }.asLiveData(viewModelScope.coroutineContext)

    @OptIn(ExperimentalCoroutinesApi::class)
    val plan = planFlow.asLiveData(viewModelScope.coroutineContext)

    @OptIn(ExperimentalCoroutinesApi::class)
    val dueDate = planFlow.transformLatest {
        if (it != null) {
            emit(DateTimeFormatter.ofPattern("MMM'' dd").format(it.endDate))
        }
    }.asLiveData(viewModelScope.coroutineContext)

    @OptIn(ExperimentalCoroutinesApi::class)
    val dueDateIn = planFlow.transformLatest {
        if (it != null) {
            val between = ChronoUnit.DAYS.between(LocalDateTime.now(), it.endDate) + 1
            emit("$between days left")
        }
    }.asLiveData(viewModelScope.coroutineContext)

    val message = MutableLiveData<String>()


    fun updatePlanState(state: PlanState) {
        val planId = plan.value?.id ?: return
        if (state == PlanState.CANCELLED && !plannedTask.value.isNullOrEmpty()) {
            message.value = "Plan is not empty"
            return
        }

        if (state == PlanState.LOCKED && plannedTask.value.isNullOrEmpty()) {
            message.value = "Plan should have at least 1 task"
            return
        }

        viewModelScope.launch {
            planRepository.updateState(planId, state)
        }
    }

    fun deletePlannedTask(taskId: Long) {
        val planId = plan.value?.id ?: return
        viewModelScope.launch {
            taskRepository.updateTaskState(taskId, TaskState.UNPLANNED)
            planRepository.deletePlannedTask(planId, taskId)
        }
    }

    fun markAsDonePlannedTask(taskId: Long) {
        viewModelScope.launch {
            val updateTaskState = taskRepository.updateTaskState(taskId, TaskState.DONE)
        }
    }

    fun getPlannedTaskById(taskId: Long): TaskWithData? {
        return plannedTask.value?.find { it.task.id == taskId }
    }

    fun getTaskGroupByDates(): List<DateWarper> {
        val planValue = plan.value ?: return listOf()
        var start = planValue.startDate.toLocalDate()
        val end = planValue.endDate.toLocalDate()
        val data = linkedMapOf<LocalDate, DateWarper>()

        while (!start.isAfter(end)) {
            data[start] = DateWarper(start)
            start = start.plusDays(1)
        }

        allTasks.value?.forEach {
            val date = it.planTask!!.eventTime.toLocalDate()
            if (data.containsKey(date)) data[date]!!.addMinutes(it.task.durationInMin!!.toLong())
        }
        return data.values.toList()
    }
}