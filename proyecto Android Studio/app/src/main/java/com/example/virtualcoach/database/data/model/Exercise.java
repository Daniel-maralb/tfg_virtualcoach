package com.example.virtualcoach.database.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.Duration;
import java.util.Objects;

@Entity
public class Exercise {
    @PrimaryKey(autoGenerate = true)
    public final long id;

    public String name;
    public String difficulty;
    public Integer duration;
    public Integer avgHR;
    public Integer steps;

    @Ignore
    public Exercise(long id, String name, String difficulty, Integer duration, Integer avgHR, Integer steps) {
        this.id = id;
        this.name = name;
        this.difficulty = difficulty;
        this.duration = duration;
        this.avgHR = avgHR;
        this.steps = steps;
    }

    public Exercise(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exercise exercise = (Exercise) o;
        return id == exercise.id &&
                Objects.equals(name, exercise.name) &&
                Objects.equals(difficulty, exercise.difficulty) &&
                Objects.equals(duration, exercise.duration) &&
                Objects.equals(avgHR, exercise.avgHR) &&
                Objects.equals(steps, exercise.steps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, difficulty, duration, avgHR, steps);
    }

    public Duration getDuration() {
        return Duration.ofMinutes(duration);
    }

    public void setDuration(Duration duration) {
        this.duration = Math.toIntExact(duration.getSeconds() / 60);
    }
}
