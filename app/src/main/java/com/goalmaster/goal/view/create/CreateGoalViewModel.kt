package com.goalmaster.goal.view.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goalmaster.goal.data.source.GoalRepository
import com.goalmaster.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreateGoalViewModel @Inject constructor(private val repository: GoalRepository) : ViewModel() {
    val name = MutableLiveData<String>()
    val total = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val dod = MutableLiveData<String>()
    val dueDate = MutableLiveData<Date?>()

    val createGoalEvent = MutableLiveData<Unit>()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    fun createGoal() {
        _dataLoading.value = true
        val nameValue = name.value!!
        val totalValue = total.value?.toInt()!!
        val dod = dod.value!!
        val dueDate  = dueDate.value!!
        val request = CreateGoalRequest(nameValue, totalValue, dod, dueDate)
        description.value?.let { request.description=it }


        viewModelScope.launch {
            val result = repository.createGoal(request)
            if (result is Result.Success) {
                createGoalEvent.value = Unit
            }
            _dataLoading.value = false
        }
    }
}