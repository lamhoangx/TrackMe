package com.lamhx.trackme.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

/**
 * The Data Access Object for the [Coordinates] class.
 * @see [WorkoutDao.getWorkoutHistory] to get list coordinates for workout
 */
@Dao
interface CoordinatesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(coordinates: List<Coordinates>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(coordinates: Coordinates)
}