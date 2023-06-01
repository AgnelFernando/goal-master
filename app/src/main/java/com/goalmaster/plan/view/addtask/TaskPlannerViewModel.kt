package com.goalmaster.plan.view.addtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.goalmaster.goal.data.entity.Goal
import com.goalmaster.task.TaskRepository
import com.goalmaster.task.data.entity.TaskWithData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject

@HiltViewModel
class TaskPlannerViewModel @Inject constructor(private val repository: TaskRepository)
    : ViewModel() {

    @ExperimentalCoroutinesApi
    val allTasks: Flow<List<TaskWithData>> = repository.observeTasksForPlan().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val taskForPlan = allTasks.transformLatest { td ->
        emit(td.filter { it.task.durationInMin != null })
    }.asLiveData(viewModelScope.coroutineContext)

}