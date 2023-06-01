package com.goalmaster.goal.view.goallist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goalmaster.goal.data.entity.Goal
import com.goalmaster.goal.data.source.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalListViewModel @Inject constructor(
    private val goalRepository: GoalRepository
): ViewModel() {

    @ExperimentalCoroutinesApi
    val goalList: Flow<List<Goal>> = goalRepository.observeActiveGoals().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateGoalProgress(id: Long, newValue: Int) {
        viewModelScope.launch {
            goalRepository.updateGoalProgress(id, newValue)
        }
    }
}