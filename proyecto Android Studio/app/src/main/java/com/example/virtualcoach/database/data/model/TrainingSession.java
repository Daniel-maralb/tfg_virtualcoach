package com.example.virtualcoach.database.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.virtualcoach.app.util.DaysOfWeekUtils;

import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.example.virtualcoach.app.util.NullUtils.isNull;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId")
})
public class TrainingSession {
    @PrimaryKey(autoGenerate = true)
    public final long id;

    @NonNull
    @ColumnInfo(index = true)
    public String userId;

    @ColumnInfo(name = "start_time")
    public Long startTime;

    @ColumnInfo(name = "modification_time")
    public long modificationTime;

    public String repetition;

    public TrainingSession(long id, @NotNull String userId, long modificationTime) {
        this.id = id;
        this.userId = userId;
        this.modificationTime = modificationTime;
    }

    @Ignore
    public TrainingSession(@NotNull String userId) {
        this.id = 0;
        this.userId = userId;
        this.modificationTime = Instant.now().getEpochSecond();
    }

    public LocalTime getStartTime() {
        return isNull(startTime) ? null
                : LocalTime.ofSecondOfDay(startTime);
    }

    public void setStartTime(LocalTime time) {
        this.startTime = (long) time.truncatedTo(ChronoUnit.MINUTES).toSecondOfDay();
    }

    public Instant getModificationTime() {
        return Instant.ofEpochSecond(modificationTime);
    }

    public void setModificationTime(Instant time) {
        this.modificationTime = time.getEpochSecond();
    }

    public void setRepetition(List<DayOfWeek> days) {
        this.repetition = DaysOfWeekUtils.getString(days);
    }

    public List<DayOfWeek> getRepetition() {
        return DaysOfWeekUtils.from(this.repetition);
    }

    public TrainingSession withId(long id) {
        return new TrainingSession(id, userId, modificationTime);
    }
}
