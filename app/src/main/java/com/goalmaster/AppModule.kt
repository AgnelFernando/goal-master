package com.goalmaster

import android.content.Context
import com.goalmaster.database.AppDatabase
import com.goalmaster.goal.data.source.DefaultGoalRepository
import com.goalmaster.goal.data.source.GoalDao
import com.goalmaster.goal.data.source.GoalRepository
import com.goalmaster.goal.data.source.LocalGoalDataSource
import com.goalmaster.notification.DefaultNotificationRepository
import com.goalmaster.notification.LocalNotificationDataSource
import com.goalmaster.notification.NotificationDao
import com.goalmaster.notification.NotificationDataSource
import com.goalmaster.notification.NotificationRepository
import com.goalmaster.plan.*
import com.goalmaster.plan.data.source.*
import com.goalmaster.task.DefaultTaskRepository
import com.goalmaster.task.data.source.LocalTaskDataSource
import com.goalmaster.task.data.source.TaskDao
import com.goalmaster.task.data.source.TaskRepository
import com.goalmaster.todo.data.source.TodoDao
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
                                   todoDao: TodoDao,
                                   ioDispatcher: CoroutineDispatcher): LocalGoalDataSource {
        return LocalGoalDataSource(goalDao, todoDao, ioDispatcher)
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
                                   planTaskDataSource: PlanTaskDataSource,
                                   goalDataSource: LocalGoalDataSource,
                                   ioDispatcher: CoroutineDispatcher): LocalTaskDataSource {
        return LocalTaskDataSource(taskDao, goalDataSource, planTaskDataSource, ioDispatcher)
    }

    @Singleton
    @Provides
    fun provideTaskRepository(dataSource: LocalTaskDataSource): TaskRepository {
        return DefaultTaskRepository(dataSource)
    }

    @Provides
    fun providePlanDao(appDatabase: AppDatabase): PlanDao {
        return appDatabase.planDao()
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
    fun providePlanRepository(dataSource: PlanDataSource,
                              taskDataSource: LocalTaskDataSource
    ): PlanRepository {
        return DefaultPlanRepository(dataSource, taskDataSource)
    }

    @Provides
    fun providePlanTaskDao(appDatabase: AppDatabase): PlanTaskDao {
        return appDatabase.planTaskDao()
    }

    @Singleton
    @Provides
    fun provideLocalPlanTaskDataSource(planTaskDao: PlanTaskDao,
                                       ioDispatcher: CoroutineDispatcher): PlanTaskDataSource {
        return LocalPlanTaskDataSource(planTaskDao, ioDispatcher)
    }

    @Singleton
    @Provides
    fun provideNotificationRepository(dataSource: NotificationDataSource
    ): NotificationRepository {
        return DefaultNotificationRepository(dataSource)
    }

    @Provides
    fun provideNotificationDao(appDatabase: AppDatabase): NotificationDao {
        return appDatabase.notificationDao()
    }

    @Singleton
    @Provides
    fun provideLocalNotificationDataSource(notificationDao: NotificationDao,
                                           ioDispatcher: CoroutineDispatcher)
    : NotificationDataSource {
        return LocalNotificationDataSource(notificationDao, ioDispatcher)
    }

    @Provides
    fun provideTodoDao(appDatabase: AppDatabase): TodoDao {
        return appDatabase.todoDao()
    }
}