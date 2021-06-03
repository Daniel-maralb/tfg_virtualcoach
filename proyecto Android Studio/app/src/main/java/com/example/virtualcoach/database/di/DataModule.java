package com.example.virtualcoach.database.di;

import android.content.Context;

import com.example.virtualcoach.database.data.AppDatabase;
import com.example.virtualcoach.database.data.dao.ActivityDataDao;
import com.example.virtualcoach.database.data.dao.ExerciseDao;
import com.example.virtualcoach.database.data.dao.TrainingSessionDao;
import com.example.virtualcoach.database.data.dao.TrainingSessionLogDao;
import com.example.virtualcoach.database.data.dao.UserDao;
import com.example.virtualcoach.database.data.repository.ActivityDataRepository;
import com.example.virtualcoach.database.data.repository.DefaultActivityDataRepository;
import com.example.virtualcoach.database.data.source.ActivityDataSource;
import com.example.virtualcoach.database.data.source.GBExportDataSource;
import com.example.virtualcoach.database.data.source.ImportableActivityDataSource;
import com.example.virtualcoach.database.data.source.LocalActivityDataSource;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

public class DataModule {

    @Module
    @InstallIn(SingletonComponent.class)
    abstract static class DefaultRepository {
        @Binds
        abstract ActivityDataRepository bindActivityDataRepository(DefaultActivityDataRepository impl);
    }

    @Module
    @InstallIn(SingletonComponent.class)
    abstract static class ImportableDataSource {
        @Binds
        abstract ImportableActivityDataSource bindImportableDataSource(GBExportDataSource impl);
    }

    @Module
    @InstallIn(SingletonComponent.class)
    abstract static class DefaultDataSource {
        @Binds
        abstract ActivityDataSource bindActivityDataSource(LocalActivityDataSource impl);
    }

    @Module
    @InstallIn(SingletonComponent.class)
    public static class Database {
        @Provides
        @Singleton
        AppDatabase provideDatabase(@ApplicationContext Context appContext) {
            return AppDatabase.create(appContext);
        }

        @Provides
        @Singleton
        ActivityDataDao provideActivityDataDao(AppDatabase database) {
            return database.activityDataDao();
        }

        @Provides
        @Singleton
        public UserDao provideUserDao(AppDatabase database) {
            return database.userDao();
        }

        @Provides
        @Singleton
        public TrainingSessionDao provideTrainingSessionDao(AppDatabase database) {
            return database.trainingSessionDao();
        }

        @Provides
        @Singleton
        public ExerciseDao provideExerciseDao(AppDatabase database) {
            return database.exerciseDao();
        }

        @Provides
        @Singleton
        public TrainingSessionLogDao provideTrainingSessionLogDao(AppDatabase database) {
            return database.trainingSessionLogDao();
        }
    }
}
