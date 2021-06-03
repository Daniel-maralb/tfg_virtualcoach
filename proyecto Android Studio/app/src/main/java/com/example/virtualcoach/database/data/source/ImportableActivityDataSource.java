package com.example.virtualcoach.database.data.source;

import java.time.LocalDateTime;

public interface ImportableActivityDataSource {
    void importData(DataImporter importer);

    boolean hasMore();

    long getSize();

    void startAt(LocalDateTime localDateTime);
}
