package com.goalmaster.todo.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Todo(@PrimaryKey(autoGenerate = true) val id : Long = 0L,
                val goalId: Long,
                var name : String,
                @ColumnInfo(name = "completed") var isCompleted: Boolean = false,

                @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
                val created: Date = Date())
