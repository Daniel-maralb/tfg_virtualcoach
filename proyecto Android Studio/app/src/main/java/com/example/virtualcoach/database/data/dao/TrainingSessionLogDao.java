package com.example.virtualcoach.database.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.virtualcoach.database.data.model.TrainingSessionLog;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

@Dao
public interface TrainingSessionLogDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertSync(TrainingSessionLog trainingSessionLog);

    @Query("SELECT * FROM TrainingSessionLog WHERE userId=:userId" +
            " ORDER BY start_time DESC")
    ListenableFuture<List<TrainingSessionLog>> getAllFromUser(String userId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    ListenableFuture<Void> saveAll(List<TrainingSessionLog> logs);
}
