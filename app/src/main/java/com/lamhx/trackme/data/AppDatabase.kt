package com.lamhx.trackme.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.lamhx.trackme.MainApplication
import com.lamhx.trackme.utilities.DATABASE_NAME
import com.lamhx.trackme.workers.TrackMeDatabaseWorker

/**
 * The Room database for this app
 */
@Database(entities = [Coordinates::class, Workout::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coordinatesDao(): CoordinatesDao
    abstract fun workoutDao(): WorkoutDao

    companion object {
        private var instance: AppDatabase? = null
        fun getInstance(): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(MainApplication.getAppContext()).also {
                    instance = it
                    it.workoutRepository = WorkoutRepository(it.workoutDao(), it.coordinatesDao())
                }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // TODO: sth
                        val request = OneTimeWorkRequestBuilder<TrackMeDatabaseWorker>().build()
                        WorkManager.getInstance(context).enqueue(request)
                    }
                })
                .build()
        }
    }

    @Volatile
    private lateinit var workoutRepository: WorkoutRepository
    fun getWorkoutRepository(): WorkoutRepository {
        return workoutRepository
    }
}