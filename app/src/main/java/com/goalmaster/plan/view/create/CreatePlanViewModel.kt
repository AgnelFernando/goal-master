package com.goalmaster.plan.view.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goalmaster.Result
import com.goalmaster.plan.data.source.PlanRepository
import com.goalmaster.plan.data.entity.Plan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class CreatePlanViewModel @Inject constructor(private val repository: PlanRepository) : ViewModel() {

    val startDate = MutableLiveData<LocalDateTime>()
    val endDate = MutableLiveData<LocalDateTime>()

    val createPlanEvent = MutableLiveData<Unit>()

    fun createPlan() {
        val startDateValue = startDate.value
        val endDateValue = endDate.value
        if (startDateValue == null || endDateValue == null) return
        val plan = Plan(startDate = startDateValue, endDate = endDateValue)

        viewModelScope.launch {
            val result = repository.savePlan(plan)
            if (result is Result.Success) {
                createPlanEvent.value = Unit
            }
        }

    }
}