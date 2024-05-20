package com.goalmaster.goal.data.source

import com.goalmaster.utils.Result
import com.goalmaster.goal.data.entity.Goal
import com.goalmaster.goal.data.entity.GoalState
import com.goalmaster.goal.data.entity.GoalWithTaskCount
import com.goalmaster.goal.view.create.CreateGoalRequest
import com.goalmaster.task.data.entity.Task
import com.goalmaster.todo.data.entity.Todo
import com.goalmaster.todo.data.source.TodoDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date

class LocalGoalDataSource(
    private val goalDao: GoalDao,
    private val todoDao: TodoDao,
    private val ioDispatcher: CoroutineDispatcher
    ): GoalDataSource {

    override suspend fun createGoal(request: CreateGoalRequest)
    : Result<Unit> = withContext(ioDispatcher) {
        return@withContext try {
            val goal = Goal(name = request.name, description = request.description,
                            definitionOfDone = request.dod, totalUnits = request.total,
                            dueDate = request.dueDate)
            goalDao.insert(goal)
            Result.Success(Unit)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override fun observeActiveGoals(state: GoalState): Flow<List<GoalWithTaskCount>> {
        return goalDao.observeActiveGoals(state)
    }

    override suspend fun updateCompletedUnits(id: Long, completed: Int)
    : Result<Unit> = withContext(ioDispatcher) {
        return@withContext try {
            val goal = goalDao.findById(id)!!
            val newCompleted = goal.completedUnits + completed
            if (newCompleted >= goal.totalUnits) {
                goal.completedUnits = goal.totalUnits
                goal.state = GoalState.COMPLETED
            } else {
                goal.completedUnits = newCompleted
            }
            goal.updatedOn = Date()
            goalDao.insert(goal)
            Result.Success(Unit)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override suspend fun getGoal(goalId: Long): Result<Goal> = withContext(ioDispatcher) {
        return@withContext try {
            val goal = goalDao.findById(goalId)
            if (goal == null) {
                Result.Error(Exception("Goal not found!"))
            } else {
                Result.Success(goal)
            }
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override fun observeGoalTasks(goalId: Long): Flow<List<Task>> {
        return goalDao.observeGoalTasks(goalId)
    }

    override fun observeGoal(goalId: Long): Flow<Goal> {
        return goalDao.observeGoal(goalId)
    }

    override suspend fun saveGoal(goal: Goal): Result<Unit> = withContext(ioDispatcher) {
        return@withContext try {
            goalDao.insert(goal)
            Result.Success(Unit)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override suspend fun delete(id: Long): Result<Unit> = withContext(ioDispatcher) {
        return@withContext try {
            val taskCount = goalDao.getGoalTaskCount(id)
            if (taskCount != 0) throw Exception("Goal isn't empty")
            goalDao.deleteById(id)
            Result.Success(Unit)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override suspend fun saveTodo(todo: Todo): Result<Unit> = withContext(ioDispatcher) {
        return@withContext try {
            todoDao.insert(todo)
            Result.Success(Unit)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override fun observeGoalTodos(goalId: Long): Flow<List<Todo>> {
        return goalDao.observeGoalTodos(goalId)
    }
}