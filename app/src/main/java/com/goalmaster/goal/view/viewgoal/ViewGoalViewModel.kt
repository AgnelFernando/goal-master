package com.goalmaster.goal.view.viewgoal

import androidx.lifecycle.*
import com.goalmaster.goal.data.source.GoalRepository
import com.goalmaster.task.data.entity.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ViewGoalViewModel @Inject constructor(private val repository: GoalRepository) : ViewModel() {
    val goalId = MutableLiveData<Long>()

    @ExperimentalCoroutinesApi
    val tasks: Flow<List<Task>> = goalId.asFlow().flatMapLatest {
        repository.observeGoalTasks(it).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

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
}