package com.example.virtualcoach.database.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.virtualcoach.database.data.model.Exercise;
import com.example.virtualcoach.database.data.model.relationships.TrainingSessionExerciseXRef;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

@Dao
public interface ExerciseDao {

    @Query("SELECT * FROM exercise")
    ListenableFuture<List<Exercise>> getAll();

    @Query("SELECT e.*" +
            " FROM TRAININGSESSIONEXERCISEXREF AS tse" +
            " INNER JOIN TrainingSession AS ts ON tse.trainingSessionId=ts.id" +
            " INNER JOIN Exercise AS e ON tse.exerciseId=e.id" +
            " WHERE ts.id=:sessionId" +
            " ORDER BY tse.position ASC"
    )
    ListenableFuture<List<Exercise>> getFromSession(long sessionId);

    @Query("SELECT e.*" +
            " FROM TRAININGSESSIONEXERCISEXREF AS tse" +
            " INNER JOIN TrainingSession AS ts ON tse.trainingSessionId=ts.id" +
            " INNER JOIN Exercise AS e ON tse.exerciseId=e.id" +
            " WHERE ts.id=:sessionId" +
            " ORDER BY tse.position ASC"
    )
    List<Exercise> getFromSessionSync(long sessionId);

    @Query("DELETE FROM TrainingSessionExerciseXRef WHERE trainingSessionId=:sessionId")
    ListenableFuture<Void> clearSession(long sessionId);

    @Insert
    ListenableFuture<List<Long>> insertAll(List<TrainingSessionExerciseXRef> trainingSessionExerciseXRefs);
}
