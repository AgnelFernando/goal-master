package com.goalmaster.task.data.source

import com.goalmaster.Result
import com.goalmaster.goal.data.source.LocalGoalDataSource
import com.goalmaster.plan.data.entity.PlanTaskStatus
import com.goalmaster.plan.data.source.PlanTaskDataSource
import com.goalmaster.task.data.entity.Task
import com.goalmaster.task.data.entity.TaskState
import com.goalmaster.task.data.entity.TaskWithData
import com.goalmaster.task.data.source.TaskDao
import com.goalmaster.task.data.source.TaskDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LocalTaskDataSource(
    private val taskDao: TaskDao,
    private val goalDataSource: LocalGoalDataSource,
    private val planTaskDataSource: PlanTaskDataSource,
    private val ioDispatcher: CoroutineDispatcher
) : TaskDataSource {

    override suspend fun saveTask(task: Task)
            : Result<Unit> = withContext(ioDispatcher) {
        return@withContext try {
            taskDao.insert(task)
            Result.Success(Unit)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override fun observeGoalTasks(goalId: Long): Flow<List<Task>> {
        return taskDao.observeGoalTasks(goalId)
    }

    override suspend fun getTask(taskId: Long): Result<Task> = withContext(ioDispatcher) {
        return@withContext try {
            val task = taskDao.findById(taskId)  ?: throw Exception("Task not found")
            Result.Success(task)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override fun observeTaskWithData(taskId: Long): Flow<TaskWithData?> {
        return taskDao.observeTaskWithDataById(taskId)
    }

    override suspend fun getTaskWithData(taskId: Long): Result<TaskWithData> = withContext(ioDispatcher) {
        return@withContext try {
            val task = taskDao.findTaskWithDataById(taskId)
            if (task == null) {
                Result.Error(Exception("Task not found!"))
            } else {
                Result.Success(task)
            }
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override suspend fun deleteTask(taskId: Long): Result<Unit> = withContext(ioDispatcher) {
        return@withContext try {
            val task = taskDao.findById(taskId) ?: throw Exception("Task not found")
            if (task.state == TaskState.PLANNED) {
                unPlanTask(task)
                task.state = TaskState.UNPLANNED
            }
            if (task.state != TaskState.UNPLANNED) throw Exception("Illegal state")
            taskDao.deleteById(taskId)
            Result.Success(Unit)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override fun observeTasksForPlan(): Flow<List<TaskWithData>> {
        return taskDao.observeTasksForPlan()
    }

    override suspend fun updateTaskState(id: Long, state: TaskState): Result<Unit> = withContext(ioDispatcher) {
        return@withContext try {
            val task = taskDao.findById(id) ?: throw Exception("Task not found")
            when (state) {
                TaskState.DONE -> doneTask(task)
                TaskState.PLANNED -> {}
                TaskState.UNPLANNED -> unPlanTask(task)
            }
            task.state = state
            taskDao.insert(task)
            Result.Success(Unit)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override suspend fun unPlanTask(task: Task): Result<Unit> {
        planTaskDataSource.updateStatusByTaskId(task.id, PlanTaskStatus.FAILED)
        return Result.Success(Unit)
    }

    override suspend fun doneTask(task: Task): Result<Unit> {
        planTaskDataSource.updateStatusByTaskId(task.id,PlanTaskStatus.COMPLETED)
        goalDataSource.updateCompletedUnits(task.goalId, task.unitSize)
        return Result.Success(Unit)
    }
}