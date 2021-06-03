package com.example.virtualcoach.database.data.source;

import com.example.virtualcoach.database.data.model.ActivityData;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class FakeImportableActivityDataSource implements ImportableActivityDataSource {

    private static final int DEFAULT_LIMIT = 100;
    private final List<ActivityData> data;
    int limit;
    int progress;

    public FakeImportableActivityDataSource(@NotNull List<ActivityData> data) {
        this(DEFAULT_LIMIT, data);
    }

    public FakeImportableActivityDataSource(int limit, @NotNull List<ActivityData> data) {
        this.data = data;
        this.limit = limit;
    }

    @NotNull
    @Override
    public List<ActivityData> readNextActivityData() {

        if (progress >= data.size()) {
            progress = 0;
            return Collections.emptyList();
        }

        List<ActivityData> result = data.subList(progress, progress + limit);

        progress += limit;

        return result;
    }

}
