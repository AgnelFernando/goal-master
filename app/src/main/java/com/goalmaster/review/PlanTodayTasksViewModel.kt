package com.goalmaster.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goalmaster.plan.data.source.PlanRepository
import com.goalmaster.task.data.entity.TaskWithData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PlanTodayTasksViewModel @Inject constructor(planRepository: PlanRepository): ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val plannedTasks = planRepository.observeCurrentPlanTasks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    ).transformLatest<List<TaskWithData>, List<TaskWithData>> { it ->
        val tomorrow = LocalDate.now().plusDays(1)
        emit(it.filter { it.planTask!!.eventTime.toLocalDate().isEqual(tomorrow) })
    }.asLiveData(viewModelScope.coroutineContext)


    fun getPlannedTaskById(taskId: Long): TaskWithData? {
        return plannedTasks.value?.find { it.task.id == taskId }
    }

}