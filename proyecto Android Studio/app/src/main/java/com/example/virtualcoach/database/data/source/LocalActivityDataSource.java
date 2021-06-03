package com.example.virtualcoach.database.data.source;

import com.example.virtualcoach.database.data.dao.ActivityDataDao;
import com.example.virtualcoach.database.data.model.ActivityData;
import com.google.common.util.concurrent.ListenableFuture;

import java.time.Instant;
import java.util.List;

import javax.inject.Inject;

public class LocalActivityDataSource implements ActivityDataSource {

    private final ActivityDataDao activityDataDao;

    @Inject
    public LocalActivityDataSource(ActivityDataDao activityDataDao) {
        this.activityDataDao = activityDataDao;
    }

    @Override
    public ListenableFuture<List<Long>> insertAll(List<ActivityData> data) {
        return activityDataDao.insertAll(data);
    }

    @Override
    public int getAvgHRSync(Instant start, Instant end) {
        return activityDataDao.getAvgHRSync(start.getEpochSecond(), end.getEpochSecond());
    }

    @Override
    public int getStepsSync(Instant start, Instant end) {
        return activityDataDao.getStepsSync(start.getEpochSecond(), end.getEpochSecond());
    }

    @Override
    public void insertAllSync(List<ActivityData> dataList) {
        activityDataDao.insertAllSync(dataList);
    }
}
