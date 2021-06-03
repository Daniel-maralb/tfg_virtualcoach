package com.example.virtualcoach.database.data.source;

import android.database.Cursor;

public interface DataImporter {
    void importData(Cursor data);
}
