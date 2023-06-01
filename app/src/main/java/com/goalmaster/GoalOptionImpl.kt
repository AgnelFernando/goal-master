package com.goalmaster

interface GoalOptionImpl {
    fun onEditClicked(id: Long)
    fun onDeleteClicked(id: Long)
    fun openUpdateProgressDialog(id: Long)
}