package com.example.virtualcoach.database.data.model.relationships;

import androidx.room.Embedded;

import com.example.virtualcoach.database.data.model.Exercise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExerciseOfSessionLog {

    public final String userId;
    public final long start_time;

    @Embedded(prefix = "e_")
    public final Exercise exercise;

    public final int position;
    public String comments;

    public ExerciseOfSessionLog(String userId, long start_time, Exercise exercise, int position) {
        this.userId = userId;
        this.start_time = start_time;
        this.exercise = exercise;
        this.position = position;
    }

    public static List<Exercise> toExerciseList(List<ExerciseOfSessionLog> data) {
        Collections.sort(data, (o1, o2) -> o1.position - o2.position);

        List<Exercise> result = new ArrayList<>(data.size());

        for (ExerciseOfSessionLog eos :
                data) {
            result.add(eos.exercise);
        }
        return result;
    }
}
