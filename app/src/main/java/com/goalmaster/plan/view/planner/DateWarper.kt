package com.goalmaster.plan.view.planner

import java.time.LocalDate
import kotlin.time.Duration.Companion.minutes

data class DateWarper(val date: LocalDate, var totalHours: Long = 0) {

    var durationInMin = 0L

    fun getId(): Long {
        return date.toEpochDay();
    }

    override fun toString(): String {
        return date.dayOfMonth.toString() + " " + date.month.toString().subSequence(0, 3)
    }

    fun getTotalTime(): String {
        return durationInMin.minutes.toString()
    }

    fun addMinutes(t: Long) { durationInMin += t}
}

