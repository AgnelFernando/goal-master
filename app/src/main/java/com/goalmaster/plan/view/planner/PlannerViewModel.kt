package com.goalmaster.plan.view.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goalmaster.Result
import com.goalmaster.plan.data.entity.Plan
import com.goalmaster.plan.data.entity.PlanState
import com.goalmaster.plan.data.source.PlanRepository
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
    private val planRepository: PlanRepository
) : ViewModel() {

    @ExperimentalCoroutinesApi
    val planFlow: Flow<Plan?> = planRepository.observeCurrentPlan().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

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
            val between = ChronoUnit.DAYS.between(LocalDateTime.now(), it.endDate)
            emit("$between days left")
        }
    }.asLiveData(viewModelScope.coroutineContext)

    fun updatePlanState(state: PlanState) {
        val planId = plan.value?.id ?: return
        viewModelScope.launch {
            planRepository.updateState(planId, state)
        }
    }

}