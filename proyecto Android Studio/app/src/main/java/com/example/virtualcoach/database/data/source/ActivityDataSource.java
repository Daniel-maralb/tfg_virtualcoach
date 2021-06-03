package com.example.virtualcoach.database.data.source;

import com.example.virtualcoach.database.data.model.ActivityData;
import com.google.common.util.concurrent.ListenableFuture;

import java.time.Instant;
import java.util.List;

public interface ActivityDataSource {

    ListenableFuture<List<Long>> insertAll(List<ActivityData> dataList);

    int getAvgHRSync(Instant start, Instant end);

    int getStepsSync(Instant start, Instant end);

    void insertAllSync(List<ActivityData> dataList);
}
