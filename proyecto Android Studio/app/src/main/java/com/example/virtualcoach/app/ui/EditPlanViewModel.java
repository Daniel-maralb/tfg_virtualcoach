package com.example.virtualcoach.app.ui;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.virtualcoach.app.di.ApplicationModule.Threads.DBExecutor;
import com.example.virtualcoach.database.data.model.Exercise;
import com.example.virtualcoach.database.data.model.TrainingSession;
import com.example.virtualcoach.database.data.model.relationships.TrainingSessionExerciseXRef;
import com.example.virtualcoach.database.data.model.relationships.TrainingSessionWithExercises;
import com.example.virtualcoach.database.data.repository.ExerciseRepository;
import com.example.virtualcoach.database.data.repository.TrainingSessionRepository;
import com.example.virtualcoach.usermanagement.di.UserManagementModule.UserManagement.CurrentUser;
import com.example.virtualcoach.usermanagement.model.DisplayUser;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

import static com.example.virtualcoach.app.util.NullUtils.isNull;

@HiltViewModel
public class EditPlanViewModel extends ViewModel {
    private final TrainingSessionRepository trainingSessionRepository;
    private final ExerciseRepository exerciseRepository;
    @Nullable
    private final DisplayUser currentUser;
    private final Executor dbExecutor;

    private List<Exercise> allExercises;
    private SettableFuture<TrainingSessionWithExercises> selected;

    @Inject
    EditPlanViewModel(TrainingSessionRepository trainingSessionRepository,
                      ExerciseRepository exerciseRepository,
                      @Nullable @CurrentUser DisplayUser currentUser,
                      @DBExecutor Executor dbExecutor) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.exerciseRepository = exerciseRepository;
        this.currentUser = currentUser;
        this.dbExecutor = dbExecutor;
    }

    public LiveData<List<TrainingSession>> getSessionsFromUser(DisplayUser currentUser) {
        return trainingSessionRepository.getAllFromUser(currentUser);
    }

    public void removeSession(TrainingSession session) {
        trainingSessionRepository.remove(session);
    }

    public void insertAll(List<TrainingSession> toInsert) {
        trainingSessionRepository.insertAll(toInsert);
    }

    public void update(TrainingSession session) {
        trainingSessionRepository.update(session);
    }

    public List<Exercise> getExercises() {

        if (isNull(allExercises)) {
            try {
                allExercises = exerciseRepository.getAll().get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return allExercises;
    }

    public void select(TrainingSession session) {
        selected = SettableFuture.create();

        if (session.id == 0)
            selected.set(new TrainingSessionWithExercises(session, Collections.emptyList()));

        Futures.addCallback(exerciseRepository.getFromSession(session), new FutureCallback<List<Exercise>>() {
            @Override
            public void onSuccess(@NullableDecl List<Exercise> result) {
                selected.set(new TrainingSessionWithExercises(session, result));
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
                selected.setException(t);
            }
        }, dbExecutor);
    }

    public void selectEmpty() {
        assert currentUser != null;
        select(new TrainingSession(currentUser.id));
    }

    public ListenableFuture<TrainingSessionWithExercises> getSelected() {
        return selected;
    }

    public Executor getDbExecutor() {
        return dbExecutor;
    }

    public void save(TrainingSessionWithExercises sessionWithExercises) {
        sessionWithExercises.session.setModificationTime(Instant.now());

        if (sessionWithExercises.session.id == 0) {
            long sessionId = 0;
            try {
                sessionId = trainingSessionRepository.insert(sessionWithExercises.session).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            sessionWithExercises.session = sessionWithExercises.session.withId(sessionId);
        } else
            trainingSessionRepository.update(sessionWithExercises.session);

        exerciseRepository.clearSession(sessionWithExercises.session);

        List<TrainingSessionExerciseXRef> sessionExerciseXRefList = new ArrayList<>(sessionWithExercises.exercises.size());
        for (int i = 0; i < sessionWithExercises.exercises.size(); i++) {
            sessionExerciseXRefList.add(
                    new TrainingSessionExerciseXRef(sessionWithExercises.session.id,
                            sessionWithExercises.exercises.get(i).id, i));
        }

        Futures.addCallback(exerciseRepository.addAll(sessionExerciseXRefList),
                new FutureCallback<List<Long>>() {
                    @Override
                    public void onSuccess(@NullableDecl List<Long> result) {
                    }

                    @Override
                    public void onFailure(@NotNull Throwable t) {
                        t.printStackTrace();
                    }
                }, dbExecutor);
    }
}