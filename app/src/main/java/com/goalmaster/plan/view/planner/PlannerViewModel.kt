package com.goalmaster.plan.view.planner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goalmaster.plan.data.entity.Plan
import com.goalmaster.plan.data.entity.PlanState
import com.goalmaster.plan.data.source.PlanRepository
import com.goalmaster.task.data.source.TaskRepository
import com.goalmaster.task.data.entity.TaskState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val planRepository: PlanRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    @ExperimentalCoroutinesApi
    val planFlow: Flow<Plan?> = planRepository.observeCurrentPlan().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val plannedTask = planRepository.observeCurrentPlanTasks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    ).asLiveData(viewModelScope.coroutineContext)

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
        if ((state == PlanState.CANCELLED || state == PlanState.COMPLETED)
            && !plannedTask.value.isNullOrEmpty()) {
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

}