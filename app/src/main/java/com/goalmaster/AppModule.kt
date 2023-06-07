package com.goalmaster

import android.content.Context
import com.goalmaster.database.AppDatabase
import com.goalmaster.goal.data.source.DefaultGoalRepository
import com.goalmaster.goal.data.source.GoalDao
import com.goalmaster.goal.data.source.GoalRepository
import com.goalmaster.goal.data.source.LocalGoalDataSource
import com.goalmaster.plan.*
import com.goalmaster.plan.data.source.*
import com.goalmaster.task.DefaultTaskRepository
import com.goalmaster.task.LocalTaskDataSource
import com.goalmaster.task.TaskDao
import com.goalmaster.task.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideGoalDao(appDatabase: AppDatabase): GoalDao {
        return appDatabase.goalDao()
    }

    @Singleton
    @Provides
    fun provideLocalGoalDataSource(goalDao: GoalDao,
                                   ioDispatcher: CoroutineDispatcher): LocalGoalDataSource {
        return LocalGoalDataSource(goalDao, ioDispatcher)
    }

    @Singleton
    @Provides
    fun provideGoalRepository(dataSource: LocalGoalDataSource): GoalRepository {
        return DefaultGoalRepository(dataSource)
    }

    @Provides
    fun provideTaskDao(appDatabase: AppDatabase): TaskDao {
        return appDatabase.taskDao()
    }

    @Singleton
    @Provides
    fun provideLocalTaskDataSource(taskDao: TaskDao,
                                   ioDispatcher: CoroutineDispatcher): LocalTaskDataSource {
        return LocalTaskDataSource(taskDao, ioDispatcher)
    }

    @Singleton
    @Provides
    fun provideTaskRepository(dataSource: LocalTaskDataSource,
                              goalDataSource: LocalGoalDataSource): TaskRepository {
        return DefaultTaskRepository(dataSource, goalDataSource)
    }

    @Provides
    fun providePlanDao(appDatabase: AppDatabase): PlanDao {
        return appDatabase.planDao()
    }

    @Provides
    fun providePlanTaskDao(appDatabase: AppDatabase): PlanTaskDao {
        return appDatabase.planTaskDao()
    }

    @Singleton
    @Provides
    fun provideLocalPlanDataSource(planDao: PlanDao,
                                   planTaskDao: PlanTaskDao,
                                   ioDispatcher: CoroutineDispatcher): PlanDataSource {
        return LocalPlanDataSource(planDao, planTaskDao, ioDispatcher)
    }

    @Singleton
    @Provides
    fun providePlanRepository(dataSource: PlanDataSource): PlanRepository {
        return DefaultPlanRepository(dataSource)
    }
}