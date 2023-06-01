package com.goalmaster.goal.view.create

import java.util.Date

data class CreateGoalRequest(val name: String, val total: Int,
                             val dod: String, var dueDate: Date,
                             var description: String? = null)
