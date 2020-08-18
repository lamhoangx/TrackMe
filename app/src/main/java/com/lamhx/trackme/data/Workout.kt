package com.lamhx.trackme.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data structure to present workout result
 * [workoutId] time finish (millis)
 * [distance] distance (meter)
 * [duration] total time
 * @see [Coordinates] hold list coordinates had recorded on workout
 */

@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey @ColumnInfo(name = "id") val workoutId: Long,
    val distance: Float,
    val duration: Long
) {}