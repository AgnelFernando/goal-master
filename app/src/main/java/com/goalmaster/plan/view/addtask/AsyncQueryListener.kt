package com.goalmaster.plan.view.addtask

interface AsyncQueryListener {
    fun onCrInsertComplete(eventId: Long)

    fun onCrUpdateComplete()
}