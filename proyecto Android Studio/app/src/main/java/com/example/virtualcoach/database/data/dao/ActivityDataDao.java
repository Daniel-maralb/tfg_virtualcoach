package com.example.virtualcoach.database.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.virtualcoach.database.data.model.ActivityData;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

@Dao
public interface ActivityDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<List<Long>> insertAll(List<ActivityData> allData);

    @Query("SELECT * FROM miband_activity_data")
    LiveData<List<ActivityData>> getAll();

    @Query("SELECT avg(heart_rate) FROM miband_activity_data WHERE heart_rate <> 255 AND heart_rate > 0 AND timestamp BETWEEN :start AND :end")
    int getAvgHRSync(long start, long end);

    @Query("SELECT sum(steps) FROM miband_activity_data WHERE timestamp BETWEEN :start AND :end")
    int getStepsSync(long start, long end);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    List<Long> insertAllSync(List<ActivityData> dataList);
}
