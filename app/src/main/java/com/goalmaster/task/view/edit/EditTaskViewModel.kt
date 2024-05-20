package com.goalmaster.task.view.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goalmaster.utils.Result
import com.goalmaster.task.data.source.TaskRepository
import com.goalmaster.task.data.entity.Task
import com.goalmaster.task.data.entity.TaskState
import com.goalmaster.task.data.entity.TaskWithData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class EditTaskViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel() {

    private var taskId: Long? = null

    val goalName = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val unitSize = MutableLiveData<String>()
    val dod = MutableLiveData<String>()
    val duration = MutableLiveData<Duration>()
    val description = MutableLiveData<String?>()

    var goalId: Long? = null

    private lateinit var currentTask: Task

    val saveTaskEvent = MutableLiveData<Unit>()
    val deleteTaskEvent = MutableLiveData<Unit>()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private var isDataLoaded = false


    fun start(taskId: Long) {
        if (_dataLoading.value == true) return

        this.taskId = taskId
        if (isDataLoaded) return

        _dataLoading.value = true

        viewModelScope.launch {
            repository.getTask(taskId).let { result ->
                if (result is Result.Success) {
                    onTaskLoaded(result.data)
                } else {
                    _dataLoading.value = false
                }
            }
        }
    }

    private fun onTaskLoaded(data: TaskWithData) {
        currentTask = data.task
        goalName.value = data.goal.name
        goalId = data.goal.id
        name.value = data.task.name
        unitSize.value = data.task.unitSize.toString()
        dod.value = data.task.definitionOfDone
        duration.value = data.task.durationInMin?.minutes
        description.value = data.task.description
        _dataLoading.value = false
        isDataLoaded = true
    }

    fun saveTask() {
        _dataLoading.value = true
        currentTask.name = name.value!!
        currentTask.unitSize = unitSize.value!!.toInt()
        currentTask.definitionOfDone = dod.value!!
        currentTask.durationInMin = duration.value?.inWholeMinutes?.toInt()
        currentTask.description = description.value

        if (currentTask.durationInMin == null && currentTask.state == TaskState.PLANNED) {
            return
        } else if (currentTask.durationInMin == null) {
            currentTask.state = TaskState.CREATED
        }
        viewModelScope.launch {
            val result = repository.saveTask(currentTask)
            if (result is Result.Success) {
                saveTaskEvent.value = Unit
            }
            _dataLoading.value = false
        }
    }

    fun deleteTask() {
        _dataLoading.value = true

        viewModelScope.launch {
            val result = repository.deleteTask(currentTask.id)
            if (result is Result.Success) {
                deleteTaskEvent.value = Unit
            }
            _dataLoading.value = false
        }
    }

    fun clearDuration() {
        currentTask.durationInMin = null
        currentTask.state = TaskState.CREATED

        viewModelScope.launch {
            val result = repository.saveTask(currentTask)
            if (result is Result.Success) {
                saveTaskEvent.value = Unit
            }
            _dataLoading.value = false
        }
    }
}