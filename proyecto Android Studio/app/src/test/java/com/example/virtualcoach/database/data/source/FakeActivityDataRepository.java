package com.example.virtualcoach.database.data.source;

import com.example.virtualcoach.database.data.model.ActivityData;
import com.example.virtualcoach.database.data.repository.ActivityDataRepository;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FakeActivityDataRepository implements ActivityDataRepository {

    public List<ListenableFuture<List<Long>>> insertAll(@NotNull ImportableActivityDataSource dataSource) {
        SettableFuture<List<Long>> future1 = SettableFuture.create();
        future1.set(List.of(-1L));

        return List.of(
                future1
        );
    }

    @Override
    public ListenableFuture<List<Long>> insertAll(List<ActivityData> dataList) {
        return null;
    }
}
