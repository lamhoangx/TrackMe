package com.lamhx.trackme.data

class WorkoutRepository(
    private val workoutDao: WorkoutDao,
    private val coordinatesDao: CoordinatesDao
) {
    val workoutHistory = workoutDao.getWorkoutHistory()
    suspend fun saveWorkout(workout: Workout, coordinates: List<Coordinates>) {
        workoutDao.insert(workout)
        coordinatesDao.insertAll(coordinates)
    }
}