package com.goalmaster.plan.view.addtask

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goalmaster.utils.Result
import com.goalmaster.plan.data.entity.Plan
import com.goalmaster.plan.data.entity.PlanTask
import com.goalmaster.plan.data.source.PlanRepository
import com.goalmaster.task.data.source.TaskRepository
import com.goalmaster.task.data.entity.TaskWithData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TaskPlannerViewModel @Inject constructor(
    repository: TaskRepository,
    private val planRepository: PlanRepository
) : ViewModel() {

    val selectedGoal = MutableStateFlow("All")

    lateinit var plan: Plan

    val createPlanTaskEvent = MutableLiveData<Unit>()

    @ExperimentalCoroutinesApi
    val allTasksFlow: Flow<List<TaskWithData>> = repository.observeTasksForPlan().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredTaskFlow: Flow<List<TaskWithData>> = selectedGoal.combine(allTasksFlow) { g, t ->
        run {
            if (g == "All") return@combine t
            else return@combine t.filter { it.goal.name == g }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val allTasks = allTasksFlow.transformLatest { td ->
        emit(td.filter { it.task.durationInMin != null }.sortedBy { it.goal.dueDate })
    }.asLiveData(viewModelScope.coroutineContext)

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksForPlan = filteredTaskFlow.transformLatest { td ->
        emit(td.filter { it.task.durationInMin != null }.sortedBy { it.goal.dueDate })
    }.asLiveData(viewModelScope.coroutineContext)


    @OptIn(ExperimentalCoroutinesApi::class)
    val availableGoals = allTasksFlow.transformLatest {
        emit(it.map { td -> td.goal }.toSet().toList())
    }.asLiveData(viewModelScope.coroutineContext)

    fun setup() {
        viewModelScope.launch {
            val result = planRepository.getCurrentPlan()
            if (result is Result.Success) {
                plan = result.data
            }
        }
    }

    fun createTaskPlan(taskId: Long, date: LocalDateTime) {
        val taskWithData = allTasks.value!!.find { it.task.id == taskId }!!
        val data = PlanTask(plan.id, taskId, date,
            taskWithData.task.durationInMin!!)
        viewModelScope.launch {
            val result = planRepository.savePlanTask(data)
            if (result is Result.Success) {
                createPlanTaskEvent.value = Unit
            }
        }
    }

}