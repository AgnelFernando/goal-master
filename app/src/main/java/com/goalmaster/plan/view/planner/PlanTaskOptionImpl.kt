package com.goalmaster.plan.view.planner

interface PlanTaskOptionImpl {

    fun onDoneClicked(taskId: Long)
    fun onViewClicked(taskId: Long)

    fun onAddEventClicked(taskId: Long)

    fun onDeleteClicked(taskId: Long)

    fun onMoveToNextDayClicked(taskId: Long)

}
