package com.goalmaster.goal.data.entity

import java.util.Date

data class GoalData(val id : Long, val name : String, val dod: String,
                    val totalUnits: Int, var completedUnits: Int,
                    val dueDate: Date, val created: Date, var updatedOn: Date,
                    val state: GoalState, val description : String?, val taskCount: Int) {
    constructor(data: GoalWithTaskCount) : this(data.goal.id, data.goal.name, data.goal.definitionOfDone, data.goal.totalUnits, data.goal.completedUnits,
    data.goal.dueDate, data.goal.created, data.goal.updatedOn, data.goal.state, data.goal.description, data.taskCount)

    var showMenu = false
}
