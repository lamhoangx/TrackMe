package com.lamhx.trackme.data

import androidx.room.Embedded
import androidx.room.Relation

/**
 * This class captures the relationship between a [Workout] and a user's [Coordinates], which is
 * used by Room to fetch the related entities.
 */
data class WorkoutHistory(
    @Embedded
    val workout: Workout,

    @Relation(parentColumn = "id", entityColumn = "workout_id")
    val listLocation: List<Coordinates> = emptyList()
) {}