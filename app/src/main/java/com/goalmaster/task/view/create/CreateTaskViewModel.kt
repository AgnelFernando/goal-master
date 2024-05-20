package com.goalmaster.task.view.create

import androidx.lifecycle.*
import com.goalmaster.utils.Result
import com.goalmaster.goal.data.entity.Goal
import com.goalmaster.goal.data.source.GoalRepository
import com.goalmaster.task.data.entity.Task
import com.goalmaster.task.data.entity.TaskState
import com.goalmaster.task.data.source.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class CreateTaskViewModel  @Inject constructor(
    private val repository: TaskRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {

    var selectedTaskCopy: String? = null
    val goalId = MutableLiveData<Long>()
    val name = MutableLiveData<String>()
    val unitSize = MutableLiveData<String>()
    val description = MutableLiveData<String?>()
    val duration = MutableLiveData<Duration>()
    val dod = MutableLiveData<String>()

    val createTaskEvent = MutableLiveData<Unit>()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    @OptIn(ExperimentalCoroutinesApi::class)
    val goal = goalId.asFlow().flatMapLatest<Long, Goal> {
        goalRepository.observeGoal(it)
    }.asLiveData(viewModelScope.coroutineContext)

    @OptIn(ExperimentalCoroutinesApi::class)
    val allTasks = goalId.asFlow().flatMapLatest {
        goalRepository.observeGoalTasks(it)
    }.asLiveData(viewModelScope.coroutineContext)

    fun createTask() {
        _dataLoading.value = true
        val nameValue = name.value!!
        val goalIdValue = goalId.value!!
        val unitSizeValue = unitSize.value!!.toInt()
        val dodValue = dod.value!!
        val task = Task(
            goalId = goalIdValue, name = nameValue, definitionOfDone =
            dodValue, unitSize = unitSizeValue,
            durationInMin = duration.value?.inWholeMinutes?.toInt(),
            description = description.value
        )
        if (task.durationInMin != null) task.state = TaskState.UNPLANNED
        viewModelScope.launch {
            val result = repository.saveTask(task)
            if (result is Result.Success) {
                createTaskEvent.value = Unit
            }
            _dataLoading.value = false
        }
    }

    fun copyTaskFromSelectedTask() {
        selectedTaskCopy?.let { selectedTaskName ->
            val allTaskValue = allTasks.value ?: return
            val selectedTask = allTaskValue.find { it.name == selectedTaskName } ?: return
            name.value = selectedTask.name
            unitSize.value = selectedTask.unitSize.toString()
            description.value = selectedTask.description
            dod.value = selectedTask.definitionOfDone
            duration.value = selectedTask.durationInMin?.minutes
        }
    }
}