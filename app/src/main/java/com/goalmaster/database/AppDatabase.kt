package com.goalmaster.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.goalmaster.goal.data.entity.Goal
import com.goalmaster.goal.data.source.GoalDao
import com.goalmaster.plan.data.entity.Plan
import com.goalmaster.plan.data.source.PlanDao
import com.goalmaster.plantask.PlanTask
import com.goalmaster.task.data.entity.Task
import com.goalmaster.task.TaskDao

/**
 * The Room database for this app
 */
@Database(entities = [Goal::class, Task::class, Plan::class, PlanTask::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(value = [RoomDatabaseConverters::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun goalDao(): GoalDao

    abstract fun taskDao(): TaskDao

    abstract fun planDao(): PlanDao

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "gm-db")
                .allowMainThreadQueries()
                .addCallback(CALLBACK)
                .fallbackToDestructiveMigration()
                .build()
        }

        private val CALLBACK = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL("""CREATE TRIGGER IF NOT EXISTS goal_on_update AFTER UPDATE ON goal
                                  BEGIN
                                        INSERT INTO goal (updated_on) VALUES ('CURRENT_TIMESTAMP');
                                  END;""")
            }
        }
    }
}

