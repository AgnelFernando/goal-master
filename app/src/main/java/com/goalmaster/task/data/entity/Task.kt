package com.goalmaster.task.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.goalmaster.task.TaskState
import java.util.Date

@Entity
data class Task(@PrimaryKey(autoGenerate = true) val id : Long = 0L,
                val goalId: Long,
                var name : String,
                var definitionOfDone: String,
                var unitSize: Int,
                var state: TaskState = TaskState.UNPLANNED,

                @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
                val created: Date = Date(),

                @ColumnInfo(name = "updated_on", defaultValue = "CURRENT_TIMESTAMP")
                var updatedOn: Date = Date(),

                var description : String?,
                var durationInMin: Int?
                )