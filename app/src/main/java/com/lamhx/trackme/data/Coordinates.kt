package com.lamhx.trackme.data

import androidx.room.*

/**
 * Data structure to present coordinates result
 * [workoutId] time to finish workout, sync with [Workout.workoutId]
 * [latitude], [longitude] coordinates had recorded
 */

@Entity(
    tableName = "coordinates",
    foreignKeys = [
        ForeignKey(entity = Workout::class, parentColumns = ["id"], childColumns = ["workout_id"])
    ],
    indices = [Index("workout_id")]
)
data class Coordinates(
    @ColumnInfo(name = "workout_id") val workoutId: Long,
    val latitude: Double = 0.toDouble(),
    val longitude: Double = 0.toDouble()
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var checkpointId: Long = 0
}