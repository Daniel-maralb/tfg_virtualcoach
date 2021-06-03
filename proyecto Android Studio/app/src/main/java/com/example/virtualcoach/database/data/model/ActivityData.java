package com.example.virtualcoach.database.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.virtualcoach.app.util.DateTimeUtils;

import java.time.Instant;

@Entity(tableName = "miband_activity_data")
public class ActivityData {
    @PrimaryKey
    public final long timestamp;

    @ColumnInfo(name = "heart_rate")
    public final int heartRate;

    public final int steps;

    public ActivityData(long timestamp, Integer heartRate, Integer steps) {
        this.timestamp = timestamp;
        this.heartRate = heartRate;
        this.steps = steps;
    }

    @Override
    @NonNull
    public String toString() {
        return "ActivityData{" +
                "timestamp=" + DateTimeUtils.formatForLog(timestamp) +
                ", heartRate=" + heartRate +
                ", steps=" + steps +
                '}';
    }

    public Instant getTimestamp() {
        return Instant.ofEpochSecond(timestamp);
    }
}
