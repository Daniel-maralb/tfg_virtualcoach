package com.example.virtualcoach.database.data.repository;

import com.example.virtualcoach.database.data.dao.TrainingSessionLogDao;
import com.example.virtualcoach.database.data.model.TrainingSessionLog;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TrainingSessionLogRepository {
    private final TrainingSessionLogDao trainingSessionLogDao;
    private final UserRepository userRepository;

    @Inject
    public TrainingSessionLogRepository(TrainingSessionLogDao trainingSessionLogDao, UserRepository userRepository) {
        this.trainingSessionLogDao = trainingSessionLogDao;
        this.userRepository = userRepository;
    }

    public ListenableFuture<List<TrainingSessionLog>> getAll() {
        return trainingSessionLogDao.getAllFromUser(userRepository.getCurrentUser().id);
    }

    public void saveAll(List<TrainingSessionLog> logs) {
        trainingSessionLogDao.saveAll(logs);
    }
}
