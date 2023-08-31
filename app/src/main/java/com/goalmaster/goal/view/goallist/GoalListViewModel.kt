package com.goalmaster.goal.view.goallist

import androidx.lifecycle.*
import com.goalmaster.goal.data.entity.Goal
import com.goalmaster.goal.data.entity.GoalState
import com.goalmaster.goal.data.source.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalListViewModel @Inject constructor(
    private val goalRepository: GoalRepository
): ViewModel() {

    val selectedState = MutableStateFlow(GoalState.OPENED)

    @ExperimentalCoroutinesApi
    val goalList = selectedState.flatMapLatest {
        goalRepository.observeActiveGoals(it).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }.asLiveData(viewModelScope.coroutineContext)


    fun updateGoalProgress(id: Long, newValue: Int) {
        viewModelScope.launch {
            goalRepository.updateGoalProgress(id, newValue)
        }
    }
}