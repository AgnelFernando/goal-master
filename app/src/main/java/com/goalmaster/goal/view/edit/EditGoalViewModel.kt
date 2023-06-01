package com.goalmaster.goal.view.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goalmaster.goal.data.entity.Goal
import com.goalmaster.goal.data.source.GoalRepository
import com.goalmaster.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class EditGoalViewModel @Inject constructor(private val repository: GoalRepository) : ViewModel() {

    private var goalId: Long? = null
    private lateinit var goal: Goal

    val name = MutableLiveData<String>()
    val total = MutableLiveData<String>()
    val completed = MutableLiveData<String>()
    val dod = MutableLiveData<String>()
    val description = MutableLiveData<String?>()
    val dueDate = MutableLiveData<Date?>()

    val saveGoalEvent = MutableLiveData<Unit>()
    val deleteGoalEvent = MutableLiveData<Unit>()

    protected val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private var isDataLoaded = false

    fun start(goalId: Long) {
        if (_dataLoading.value == true) return

        this.goalId = goalId
        if (isDataLoaded) return

        _dataLoading.value = true

        viewModelScope.launch {
            repository.getGoal(goalId).let { result ->
                if (result is Result.Success) {
                    onGoalLoaded(result.data)
                } else {
                    _dataLoading.value = false
                }
            }
        }
    }

    private fun onGoalLoaded(data: Goal) {
        goal = data
        name.value = data.name
        description.value = data.description
        total.value = data.totalUnits.toString()
        completed.value = data.completedUnits.toString()
        dod.value = data.definitionOfDone
        dueDate.value = data.dueDate
        _dataLoading.value = false
        isDataLoaded = true
    }

    fun saveGoal() {
        _dataLoading.value = true
        goal.name = name.value!!
        goal.totalUnits = total.value?.toInt()!!
        goal.completedUnits = completed.value?.toInt()!!
        goal.definitionOfDone = dod.value!!
        goal.dueDate = dueDate.value!!
        goal.description = description.value

        viewModelScope.launch {
            val result = repository.saveGoal(goal)
            if (result is Result.Success) {
                saveGoalEvent.value = Unit
            }
            _dataLoading.value = false
        }
    }

    fun deleteGoal() {
        _dataLoading.value = true

        viewModelScope.launch {
            val result = repository.deleteGoal(goal.id)
            if (result is Result.Success) {
                deleteGoalEvent.value = Unit
            }
            _dataLoading.value = false
        }
    }
}