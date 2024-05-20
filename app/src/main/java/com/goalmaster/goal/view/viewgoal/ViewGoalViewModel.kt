package com.goalmaster.goal.view.viewgoal

import androidx.lifecycle.*
import com.goalmaster.goal.data.entity.GoalState
import com.goalmaster.goal.data.source.GoalRepository
import com.goalmaster.task.data.entity.Task
import com.goalmaster.task.data.entity.TaskState
import com.goalmaster.todo.data.entity.Todo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ViewGoalViewModel @Inject constructor(private val repository: GoalRepository) : ViewModel() {
    val goalId = MutableLiveData<Long>()
    val selectedState = MutableStateFlow(0)
    val todoName = MutableLiveData<String>()
    val showAddTodo = MutableLiveData(false)

    @ExperimentalCoroutinesApi
    val allTasks: Flow<List<Task>> = goalId.asFlow().flatMapLatest {
        repository.observeGoalTasks(it).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    @ExperimentalCoroutinesApi
    val allTodos1: Flow<List<Todo>> = goalId.asFlow().flatMapLatest {
        repository.observeGoalTodos(it).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: Flow<List<Task>> = selectedState.combine(allTasks) { s, t ->
        run {
            if (s == 0) return@combine t.filter { it.state != TaskState.DONE }
            else return@combine t.filter { it.state == TaskState.DONE }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val allTodos = selectedState.combine(allTodos1) { s, t ->
        run {
            if (s == 0) return@combine t.filter { !it.isCompleted }
            else return@combine t.filter { it.isCompleted  }
        }
    }.asLiveData(viewModelScope.coroutineContext)



    @OptIn(ExperimentalCoroutinesApi::class)
    val isEmpty = tasks.transformLatest <List<Task>, Boolean> {
        it.isEmpty()
    }.asLiveData(viewModelScope.coroutineContext)

    @OptIn(ExperimentalCoroutinesApi::class)
    val goalFlow = goalId.asFlow().flatMapLatest { repository.observeGoal(it) }

    val goal = goalFlow.asLiveData(viewModelScope.coroutineContext)

    @OptIn(ExperimentalCoroutinesApi::class)
    val progressPercentage = goalFlow.transformLatest {
        var result = 0
        if (it.completedUnits != 0) {
            result = ((it.completedUnits.toDouble() / it.totalUnits.toDouble()) * 100).toInt()
        }
        emit(result)
    }.asLiveData(viewModelScope.coroutineContext)

    fun setUp(goalId: Long) {
        this.goalId.value = goalId
    }

    fun completeTask(todo: Todo, completed: Boolean) = viewModelScope.launch {
        if (completed) {
            todo.isCompleted = true
            repository.saveTodo(todo)
        }
    }

    fun createTodo() {
        if (showAddTodo.value == false || todoName.value.isNullOrEmpty()) return
        val todo = Todo(goalId = goalId.value!!, name = todoName.value!!)
        viewModelScope.launch {
            repository.saveTodo(todo)
            showAddTodo.value = false
            todoName.value = ""
        }
    }
}