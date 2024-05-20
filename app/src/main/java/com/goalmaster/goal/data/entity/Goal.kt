package com.goalmaster.goal.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Goal(@PrimaryKey(autoGenerate = true) val id : Long = 0L,
                var name : String,

                @ColumnInfo(defaultValue = "")
                var definitionOfDone: String,
                var totalUnits: Int,

                @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
                var dueDate: Date,

                var state: GoalState = GoalState.ACTIVE,

                @ColumnInfo(defaultValue = "0")
                var completedUnits: Int = 0,

                @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
                val created: Date = Date(),

                @ColumnInfo(name = "updated_on", defaultValue = "CURRENT_TIMESTAMP")
                var updatedOn: Date = Date(),

                var description : String?
)