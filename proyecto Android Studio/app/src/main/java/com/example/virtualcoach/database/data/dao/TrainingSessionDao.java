package com.example.virtualcoach.database.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.virtualcoach.database.data.model.TrainingSession;
import com.example.virtualcoach.database.data.model.relationships.TrainingSessionExerciseXRef;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

@Dao
public interface TrainingSessionDao {
    @Insert
    ListenableFuture<Long> insert(TrainingSession session);

    @Query("SELECT * FROM TRAININGSESSION WHERE userId=:id")
    LiveData<List<TrainingSession>> getAllFromUser(String id);

    @Query("SELECT * FROM TRAININGSESSION WHERE userId=:id")
    List<TrainingSession> getAllFromUserSync(String id);

    @Delete
    ListenableFuture<Void> delete(TrainingSession session);

    @Query("SELECT * FROM TRAININGSESSION WHERE id=:id")
    LiveData<TrainingSession> getById(long id);

    @Update
    ListenableFuture<Void> update(TrainingSession trainingSession);

    @Update
    void updateAllSync(List<TrainingSession> sessions);

    @Delete
    ListenableFuture<Void> delete(TrainingSessionExerciseXRef ref);

    @Insert
    ListenableFuture<Long> insert(TrainingSessionExerciseXRef ref);

    @Update
    ListenableFuture<Void> update(TrainingSessionExerciseXRef ref);

    @Insert
    ListenableFuture<Void> insertAll(List<TrainingSession> toInsert);
}
