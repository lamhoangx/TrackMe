package com.lamhx.trackme.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * The Data Access Object for the [Workout] class.
 */
@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workouts ORDER BY id")
    fun getPlants(): LiveData<List<Workout>>

    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    fun getPlant(workoutId: String): LiveData<Workout>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: Workout)

    /**
     * This query will tell Room to query both the [Workout] and [Coordinates] tables and handle
     * the object mapping.
     */
    @Transaction
    @Query("SELECT * FROM workouts WHERE id IN (SELECT DISTINCT(workout_id) FROM coordinates) ORDER BY id DESC")
    fun getWorkoutHistory(): LiveData<List<WorkoutHistory>>

    /**
     * This query will tell Room to query both the [Workout] and [Coordinates] tables and handle
     * the object mapping.
     * Limit num-list and index to get
     */
    @Transaction
    @Query("SELECT * FROM workouts WHERE id IN (SELECT DISTINCT(workout_id) FROM coordinates) ORDER BY id DESC LIMIT :index,:limit")
    fun getWorkoutHistory(limit: Long, index: Long): LiveData<List<WorkoutHistory>>
}