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
import com.goalmaster.task.data.entity.TaskTimeTracker
import com.goalmaster.task.data.source.TaskDao
import com.goalmaster.todo.data.entity.Todo
import com.goalmaster.todo.data.source.TodoDao

/**
 * The Room database for this app
 */
@Database(entities = [Goal::class, Task::class, Plan::class,
    PlanTask::class, TaskTimeTracker::class, Todo::class],
    version = 6,
    exportSchema = true
)
@TypeConverters(value = [RoomDatabaseConverters::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun goalDao(): GoalDao

    abstract fun taskDao(): TaskDao

    abstract fun todoDao(): TodoDao

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

        private val MIGRATION = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS `Todo`")
                database.execSQL("CREATE TABLE Todo (" +
                        "id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT," +
                        "goalId INTEGER NOT NULL," +
                        "name TEXT NOT NULL," +
                        "completed INTEGER NOT NULL," +
                        "created INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP)")
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "gm-db")
                .allowMainThreadQueries()
                .addCallback(CALLBACK)
                .addMigrations(MIGRATION)
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

