package com.goalmaster.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.goalmaster.goal.data.entity.Goal
import com.goalmaster.goal.data.source.GoalDao
import com.goalmaster.plan.data.entity.Plan
import com.goalmaster.plan.data.source.PlanDao
import com.goalmaster.plan.data.entity.PlanTask
import com.goalmaster.plan.data.source.PlanTaskDao
import com.goalmaster.task.data.entity.Task
import com.goalmaster.task.data.source.TaskDao

/**
 * The Room database for this app
 */
@Database(entities = [Goal::class, Task::class, Plan::class, PlanTask::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(value = [RoomDatabaseConverters::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun goalDao(): GoalDao

    abstract fun taskDao(): TaskDao

    abstract fun planDao(): PlanDao

    abstract fun planTaskDao(): PlanTaskDao

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE PlanTask")
                database.execSQL("CREATE TABLE PlanTask (" +
                        "planId INTEGER NOT NULL," +
                        "taskId INTEGER NOT NULL," +
                        "eventTime TEXT NOT NULL," +
                        "durationInMinutes INTEGER NOT NULL," +
                        "eventId INTEGER DEFAULT 0," +
                        "status TEXT NOT NULL," +
                        "PRIMARY KEY (planId, taskId))")
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "gm-db")
                .allowMainThreadQueries()
                .addCallback(CALLBACK)
                .addMigrations(MIGRATION_1_2)
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

