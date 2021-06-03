package com.example.virtualcoach.database.data.model.relationships;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import com.example.virtualcoach.database.data.model.Exercise;
import com.example.virtualcoach.database.data.model.TrainingSessionLog;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        primaryKeys = {"sessionLogId", "exerciseId"},
        foreignKeys = {
                @ForeignKey(
                        entity = TrainingSessionLog.class,
                        onDelete = CASCADE,
                        parentColumns = "id",
                        childColumns = "sessionLogId"
                ),
                @ForeignKey(
                        entity = Exercise.class,
                        parentColumns = "id",
                        childColumns = "exerciseId"
                )
        },
        indices = {
                @Index(value = {"sessionLogId"}),
                @Index(value = {"exerciseId"})
        }
)
public class TrainingSessionLogExerciseXRef {
    public final long sessionLogId;
    public final long exerciseId;

    public int position;
    public String observations;

    public TrainingSessionLogExerciseXRef(long sessionLogId, long exerciseId, int position) {
        this.sessionLogId = sessionLogId;
        this.exerciseId = exerciseId;
        this.position = position;
    }

    @Ignore
    public TrainingSessionLogExerciseXRef(long sessionLogId, long exerciseId, int position, String observations) {
        this.sessionLogId = sessionLogId;
        this.exerciseId = exerciseId;
        this.position = position;
        this.observations = observations;
    }
}

