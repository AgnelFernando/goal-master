package com.goalmaster.analytics

import androidx.lifecycle.*
import com.goalmaster.plan.data.entity.PlanTaskStatus
import com.goalmaster.plan.data.source.PlanRepository
import com.goalmaster.task.data.entity.TaskWithData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(planRepository: PlanRepository) : ViewModel() {

    private val plannedTaskFlow = planRepository.observeCurrentPlanAllTasks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val plannedGoals = plannedTaskFlow.mapLatest { tasks ->
        tasks.groupBy({ it.goal }, { it.task.durationInMin ?: 0 })
            .mapValues { (_, durations) -> durations.sum() }
    }.asLiveData(viewModelScope.coroutineContext)


    @OptIn(ExperimentalCoroutinesApi::class)
    val plannedTasksGoals = plannedTaskFlow.transformLatest<List<TaskWithData>, List<TaskWithData>> {
        emit(it.filter { t -> t.planTask?.status != PlanTaskStatus.FAILED })
    }.asLiveData(viewModelScope.coroutineContext)

}