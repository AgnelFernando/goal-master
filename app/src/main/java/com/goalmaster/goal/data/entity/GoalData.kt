package com.goalmaster.goal.data.entity

import java.util.Date

data class GoalData(val id : Long, val name : String, val dod: String,
                    val totalUnits: Int, var completedUnits: Int,
                    val dueDate: Date, val created: Date, var updatedOn: Date,
                    val state: GoalState, val description : String?,) {

    constructor(g: Goal) : this(g.id, g.name, g.definitionOfDone, g.totalUnits, g.completedUnits,
    g.dueDate, g.created, g.updatedOn, g.state, g.description)

    var showMenu = false
}
