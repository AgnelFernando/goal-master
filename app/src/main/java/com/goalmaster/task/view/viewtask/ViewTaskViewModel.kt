package com.goalmaster.task.view.viewtask

import androidx.lifecycle.*
import com.goalmaster.task.data.source.TaskRepository
import com.goalmaster.task.data.entity.TaskState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class ViewTaskViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel() {

    val taskId = MutableLiveData<Long>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val taskWithDataFlow = taskId.asFlow().flatMapLatest { repository.observeTaskWithData(it) }

    @OptIn(ExperimentalCoroutinesApi::class)
    val task = taskWithDataFlow.transformLatest {
        if (it != null) emit(it.task)
    }.asLiveData(viewModelScope.coroutineContext)

    @OptIn(ExperimentalCoroutinesApi::class)
    val taskDuration = taskWithDataFlow.transformLatest {
        if (it?.task?.durationInMin != null && it.task.state == TaskState.UNPLANNED) {
            emit(it.task.durationInMin!!.minutes.toString())
        }
    }.asLiveData(viewModelScope.coroutineContext)

    @OptIn(ExperimentalCoroutinesApi::class)
    val taskDescription = taskWithDataFlow.transformLatest {
        if (it?.task?.description != null) {
            emit(it.task.description)
        }
    }.asLiveData(viewModelScope.coroutineContext)

    @OptIn(ExperimentalCoroutinesApi::class)
    val goal = taskWithDataFlow.transformLatest {
        if (it != null) emit(it.goal)
    }.asLiveData(viewModelScope.coroutineContext)

    @OptIn(ExperimentalCoroutinesApi::class)
    val planTask = taskWithDataFlow.transformLatest {
        if (it?.planTask != null) emit(it.planTask)
    }.asLiveData(viewModelScope.coroutineContext)

    fun updateTaskState(state: TaskState) {
        val id = taskId.value!!
        viewModelScope.launch {
            repository.updateTaskState(id, state)
        }
    }

}