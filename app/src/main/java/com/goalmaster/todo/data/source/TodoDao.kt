package com.goalmaster.todo.data.source

import androidx.room.Dao
import com.goalmaster.database.BaseDao
import com.goalmaster.todo.data.entity.Todo

@Dao
interface TodoDao : BaseDao<Todo>