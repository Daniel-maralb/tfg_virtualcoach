package com.example.virtualcoach.database.data.repository;

import com.example.virtualcoach.database.data.model.ActivityData;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

public interface ActivityDataRepository {

    ListenableFuture<List<Long>> insertAll(List<ActivityData> dataList);

    void insertAllSync(List<ActivityData> dataList);
}
