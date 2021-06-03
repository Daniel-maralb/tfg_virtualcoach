package com.example.virtualcoach.database.data.repository;

import com.example.virtualcoach.database.data.model.ActivityData;
import com.example.virtualcoach.database.data.source.ActivityDataSource;
import com.google.common.util.concurrent.ListenableFuture;

import java.time.Instant;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DefaultActivityDataRepository implements ActivityDataRepository {
    private final ActivityDataSource localDataSource;

    @Inject
    public DefaultActivityDataRepository(ActivityDataSource localDataSource) {
        this.localDataSource = localDataSource;
    }

    @Override
    public ListenableFuture<List<Long>> insertAll(List<ActivityData> dataList) {
        return localDataSource.insertAll(dataList);
    }

    @Override
    public void insertAllSync(List<ActivityData> dataList) {
        localDataSource.insertAllSync(dataList);
    }

    public int getAvgHRSync(Instant start, Instant end) {
        return localDataSource.getAvgHRSync(start, end);
    }

    public int getStepsSync(Instant start, Instant end) {
        return localDataSource.getStepsSync(start, end);
    }
}
