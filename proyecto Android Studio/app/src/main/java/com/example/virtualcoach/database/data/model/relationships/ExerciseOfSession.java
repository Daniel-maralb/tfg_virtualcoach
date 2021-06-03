package com.example.virtualcoach.database.data.model.relationships;

import androidx.room.Embedded;

import com.example.virtualcoach.database.data.model.Exercise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExerciseOfSession {

    public final long sessionId;

    @Embedded(prefix = "e_")
    public final Exercise exercise;

    public final int position;

    public ExerciseOfSession(long sessionId, Exercise exercise, int position) {
        this.sessionId = sessionId;
        this.exercise = exercise;
        this.position = position;
    }

    public static List<Exercise> toExerciseList(List<ExerciseOfSession> data) {
        Collections.sort(data, (o1, o2) -> o1.position - o2.position);

        List<Exercise> result = new ArrayList<>(data.size());

        for (ExerciseOfSession eos :
                data) {
            result.add(eos.exercise);
        }

        return result;
    }
}
