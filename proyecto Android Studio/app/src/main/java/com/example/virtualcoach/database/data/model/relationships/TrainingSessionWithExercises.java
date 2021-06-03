package com.example.virtualcoach.database.data.model.relationships;

import com.example.virtualcoach.database.data.model.Exercise;
import com.example.virtualcoach.database.data.model.TrainingSession;

import java.util.List;

public class TrainingSessionWithExercises {
    public TrainingSession session;
    public List<Exercise> exercises;

    public TrainingSessionWithExercises(TrainingSession session, List<Exercise> exercises) {
        this.session = session;
        this.exercises = exercises;
    }
}
