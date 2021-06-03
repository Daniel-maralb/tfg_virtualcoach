package com.example.virtualcoach.database.data.repository;

import androidx.lifecycle.LiveData;

import com.example.virtualcoach.app.di.ApplicationModule.Threads.DBExecutor;
import com.example.virtualcoach.database.data.dao.TrainingSessionDao;
import com.example.virtualcoach.database.data.model.Exercise;
import com.example.virtualcoach.database.data.model.TrainingSession;
import com.example.virtualcoach.database.data.model.relationships.ExerciseOfSession;
import com.example.virtualcoach.database.data.model.relationships.TrainingSessionExerciseXRef;
import com.example.virtualcoach.usermanagement.model.DisplayUser;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TrainingSessionRepository {

    private final TrainingSessionDao trainingSessionDao;
    private final Executor executor;

    @Inject
    public TrainingSessionRepository(TrainingSessionDao trainingSessionDao, @DBExecutor Executor executor) {
        this.trainingSessionDao = trainingSessionDao;
        this.executor = executor;
    }

    public List<TrainingSession> getAllFromUserSync(DisplayUser user) {
        return trainingSessionDao.getAllFromUserSync(user.id);
    }

    public LiveData<List<TrainingSession>> getAllFromUser(DisplayUser user) {
        return trainingSessionDao.getAllFromUser(user.id);
    }

    public ListenableFuture<Void> remove(TrainingSession session) {
        return trainingSessionDao.delete(session);
    }

    public ListenableFuture<Void> update(TrainingSession trainingSession) {
        return trainingSessionDao.update(trainingSession);
    }

    public void updateAllSync(List<TrainingSession> sessions) {
        trainingSessionDao.updateAllSync(sessions);
    }

    public void removeExerciseFrom(long sessionId, Exercise exercise, int position) {
        trainingSessionDao.delete(new TrainingSessionExerciseXRef(sessionId, exercise.id, position));
    }

    public void addExerciseTo(long sessionId, long exerciseId, int position) {
        trainingSessionDao.insert(new TrainingSessionExerciseXRef(sessionId, exerciseId, position));
    }

    public LiveData<TrainingSession> getById(long sessionId) {
        return trainingSessionDao.getById(sessionId);
    }

    public void update(ExerciseOfSession eos) {
        trainingSessionDao.update(new TrainingSessionExerciseXRef(eos.sessionId, eos.exercise.id, eos.position));
    }

    public ListenableFuture<Long> insert(TrainingSession session) {
        return trainingSessionDao.insert(session);
    }

    public ListenableFuture<Void> insertAll(List<TrainingSession> toInsert) {
        return trainingSessionDao.insertAll(toInsert);
    }
}
