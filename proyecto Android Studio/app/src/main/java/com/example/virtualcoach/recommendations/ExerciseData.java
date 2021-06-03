package com.example.virtualcoach.recommendations;

import com.example.virtualcoach.database.data.model.Exercise;

public class ExerciseData {
    public Exercise exercise;

    public ExerciseData(Exercise exercise) {
        this.exercise = exercise;
    }

    public boolean isNotIntenseEnough(UserData userData) {
        return exercise.avgHR < userData.hrLow;
    }

    public boolean isTooIntense(UserData userData) {
        return exercise.avgHR > userData.hrTooHigh;
    }

    public boolean isIntense(UserData userData) {
        return userData.hrHigh < exercise.avgHR && exercise.avgHR < userData.hrTooHigh;
    }

    public boolean isNotIntense(UserData userData) {
        return userData.hrLow < exercise.avgHR && exercise.avgHR < userData.hrHigh;
    }
}
