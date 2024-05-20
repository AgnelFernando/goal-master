package com.goalmaster.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goalmaster.plan.data.source.PlanRepository
import com.goalmaster.task.data.entity.TaskState
import com.goalmaster.task.data.entity.TaskWithData
import com.goalmaster.task.data.source.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReviewTaskViewModel @Inject constructor(
    private val planRepository: PlanRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val plannedTasks = planRepository.observeCurrentPlanTasks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    ).transformLatest<List<TaskWithData>, List<TaskWithData>> { it ->
        val today = LocalDate.now()
        emit(it.filter { it.planTask!!.eventTime.toLocalDate().isEqual(today) })
    }.asLiveData(viewModelScope.coroutineContext)

    fun markAsDonePlannedTask(taskId: Long) {
        viewModelScope.launch {
            taskRepository.updateTaskState(taskId, TaskState.DONE)
        }
    }

    fun moveTaskPlanToNextDay(taskId: Long) {
        val pTask = plannedTasks.value!!.find {
            it.task.id == taskId
        }?.planTask ?: return
        pTask.eventTime = pTask.eventTime.plusDays(1)
        viewModelScope.launch {
            planRepository.savePlanTask(pTask)
        }
    }

}