package com.example.virtualcoach.database.data.repository;

import com.example.virtualcoach.database.data.dao.ExerciseDao;
import com.example.virtualcoach.database.data.model.Exercise;
import com.example.virtualcoach.database.data.model.TrainingSession;
import com.example.virtualcoach.database.data.model.relationships.TrainingSessionExerciseXRef;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ExerciseRepository {
    private final ExerciseDao exerciseDao;

    @Inject
    public ExerciseRepository(ExerciseDao exerciseDao) {
        this.exerciseDao = exerciseDao;
    }

    public ListenableFuture<List<Exercise>> getAll() {
        return exerciseDao.getAll();
    }

    public List<Exercise> getFromSessionSync(TrainingSession session) {
        return exerciseDao.getFromSessionSync(session.id);
    }

    public ListenableFuture<List<Exercise>> getFromSession(TrainingSession session) {
        return exerciseDao.getFromSession(session.id);
    }

    public ListenableFuture<Void> clearSession(TrainingSession session) {
        return exerciseDao.clearSession(session.id);
    }

    public ListenableFuture<List<Long>> addAll(List<TrainingSessionExerciseXRef> trainingSessionExerciseXRefs) {
        return exerciseDao.insertAll(trainingSessionExerciseXRefs);
    }
}
