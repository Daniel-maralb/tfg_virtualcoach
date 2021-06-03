package com.example.virtualcoach.database.data.source;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.inject.Inject;

import static com.example.virtualcoach.app.di.ApplicationModule.Singletons.PathToGBExport;
import static com.example.virtualcoach.app.util.NullUtils.isNull;

public class GBExportDataSource implements ImportableActivityDataSource {
    private static final Logger LOG = LoggerFactory.getLogger(GBExportDataSource.class);

    public static final String COL_HR = "HEART_RATE";
    public static final String COL_TIMESTAMP = "TIMESTAMP";
    public static final String COL_STEPS = "STEPS";
    private static final int DEFAULT_LIMIT = 20000;

    private final String path;
    private final int limit;

    private SQLiteDatabase db;
    private int offset;
    private Long size;

    private String selectionFilter;

    @Inject
    public GBExportDataSource(@PathToGBExport String path) {
        this(path, DEFAULT_LIMIT);
    }

    private GBExportDataSource(String path, int limit) {
        this.path = path;
        this.limit = limit;
    }

    @Override
    public void importData(DataImporter importer) {
        if (isNull(db))
            openDB();

        LOG.debug("Preparing chunk of maxsize " + limit + " from GBDB");

        Cursor cursor = db.query(
                "MI_BAND_ACTIVITY_SAMPLE",
                null,
                selectionFilter,
                null,
                null,
                null,
                "timestamp ASC",
                offset + "," + limit
        );

        importer.importData(cursor);

        cursor.close();
        offset += limit;

        if (!hasMore())
            closeDB();
    }

    public void startAt(LocalDateTime localDateTime) {
        this.selectionFilter = "timestamp > " + localDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    @Override
    public boolean hasMore() {
        return offset < getSize();
    }

    @Override
    public long getSize() {
        if (isNull(size))
            size = calculateSize();

        return size;
    }

    private void openDB() {
        offset = 0;
        db = SQLiteDatabase.
                openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
    }

    private void closeDB() {
        db.close();
        db = null;
    }

    private long calculateSize() {
        if (isNull(db))
            openDB();

        return DatabaseUtils.queryNumEntries(db, "MI_BAND_ACTIVITY_SAMPLE", selectionFilter);
    }
}
