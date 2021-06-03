package com.example.virtualcoach.app.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.virtualcoach.app.di.ApplicationModule.Threads.DBExecutor;
import com.example.virtualcoach.database.data.model.TrainingSessionLog;
import com.example.virtualcoach.database.data.repository.TrainingSessionLogRepository;
import com.example.virtualcoach.database.data.repository.UserRepository;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TrainingProgressViewModel extends ViewModel {

    private final TrainingSessionLogRepository trainingSessionLogRepository;
    private final Executor dbExecutor;
    private final UserRepository userRepository;

    private final MutableLiveData<List<TrainingSessionLog>> sessionLogs = new MutableLiveData<>();

    @Inject
    public TrainingProgressViewModel(TrainingSessionLogRepository trainingSessionLogRepository, @DBExecutor Executor dbExecutor, UserRepository userRepository) {
        this.trainingSessionLogRepository = trainingSessionLogRepository;
        this.dbExecutor = dbExecutor;
        this.userRepository = userRepository;
    }

    public LiveData<List<TrainingSessionLog>> getCurrentUserLogs() {
        updateSessionLogs();
        return sessionLogs;
    }

    private void updateSessionLogs() {
        dbExecutor.execute(() -> {
            Futures.addCallback(trainingSessionLogRepository.getAll(), new FutureCallback<List<TrainingSessionLog>>() {
                @Override
                public void onSuccess(@NullableDecl List<TrainingSessionLog> result) {
                    sessionLogs.postValue(result);
                }

                @Override
                public void onFailure(@NotNull Throwable t) {
                    t.printStackTrace();
                }
            }, dbExecutor);
        });

    }

    public boolean isLoggedIn() {
        return userRepository.isLoggedIn();
    }
}