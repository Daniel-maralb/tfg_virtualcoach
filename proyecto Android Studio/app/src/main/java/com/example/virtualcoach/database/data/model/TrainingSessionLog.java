package com.example.virtualcoach.database.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId")
        },
        indices = {@Index(unique = true, value = {"userId", "start_time"})}
)
public class TrainingSessionLog {
    @PrimaryKey(autoGenerate = true)
    public final long id;

    @NonNull
    @ColumnInfo(index = true)
    public String userId;

    @ColumnInfo(name = "start_time")
    public long startTime;

    public String comment;

    public TrainingSessionLog(long id, @NotNull String userId, long startTime) {
        this.id = id;
        this.userId = userId;
        this.startTime = startTime;
    }

    @Ignore
    public TrainingSessionLog(@NotNull String userId, Instant startTime, String comment) {
        this.id = 0;
        this.userId = userId;
        setStartTime(startTime);
        this.comment = comment;
    }

    public Instant getStartTime() {
        return Instant.ofEpochSecond(startTime);
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime.getEpochSecond();
    }
}
