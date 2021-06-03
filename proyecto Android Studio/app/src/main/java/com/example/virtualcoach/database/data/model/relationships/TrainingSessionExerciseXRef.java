package com.example.virtualcoach.database.data.model.relationships;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import com.example.virtualcoach.database.data.model.Exercise;
import com.example.virtualcoach.database.data.model.TrainingSession;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        primaryKeys = {"trainingSessionId", "exerciseId"},
        foreignKeys = {
                @ForeignKey(
                        entity = TrainingSession.class,
                        onDelete = CASCADE,
                        parentColumns = "id",
                        childColumns = "trainingSessionId"
                ),
                @ForeignKey(
                        entity = Exercise.class,
                        parentColumns = "id",
                        childColumns = "exerciseId"
                )
        },
        indices = {
                @Index(value = {"trainingSessionId"}),
                @Index(value = {"exerciseId"})
        }
)
public class TrainingSessionExerciseXRef {
    public final long trainingSessionId;
    public final long exerciseId;

    public int position;

    public TrainingSessionExerciseXRef(long trainingSessionId, long exerciseId) {
        this.trainingSessionId = trainingSessionId;
        this.exerciseId = exerciseId;
    }

    @Ignore
    public TrainingSessionExerciseXRef(long trainingSessionId, long exerciseId, int position) {
        this.trainingSessionId = trainingSessionId;
        this.exerciseId = exerciseId;
        this.position = position;
    }
}

