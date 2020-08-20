package com.lamhx.trackme.data

import androidx.lifecycle.LiveData
import androidx.room.Transaction

class WorkoutRepository(
    private val workoutDao: WorkoutDao,
    private val coordinatesDao: CoordinatesDao
) {
    val workoutHistory = workoutDao.getWorkoutHistory()
    suspend fun saveWorkout(workout: Workout, coordinates: List<Coordinates>) {
        workoutDao.insert(workout)
        coordinatesDao.insertAll(coordinates)
    }

    suspend fun getWorkoutHistory(workoutId: Long): LiveData<WorkoutHistory?> {
        return workoutDao.getWorkoutHistory(workoutId)
    }

    suspend fun deleteWorkout(workoutHistory: WorkoutHistory?) {
        workoutHistory?.let {
            coordinatesDao.delete(it.listLocation)
            workoutDao.deleteWorkout(it.workout)
        }
    }
}