package com.example.virtualcoach.database.data.source;

import com.example.virtualcoach.database.data.model.ActivityData;
import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class FakeActivityDataSource implements ActivityDataSource {

    private final List<ActivityData> data;

    public FakeActivityDataSource(List<ActivityData> data) {
        this.data = new ArrayList<>(data);
    }

    @Override
    public ListenableFuture<List<Long>> insertAll(List<ActivityData> newData) {
        data.addAll(newData);
        return new ListenableFuture<List<Long>>() {
            @Override
            public void addListener(@NotNull Runnable listener, @NotNull Executor executor) {

            }

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public List<Long> get() {
                return new ArrayList<>();
            }

            @Override
            public List<Long> get(long timeout, TimeUnit unit) {
                return new ArrayList<>();
            }
        };
    }

    public List<ActivityData> getData() {
        return data;
    }
}
