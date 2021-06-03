package com.example.virtualcoach.tests.e2e.usermanagement;

import android.content.Context;

import androidx.room.Room;

import com.example.virtualcoach.database.data.AppDatabase;
import com.example.virtualcoach.database.data.dao.ActivityDataDao;
import com.example.virtualcoach.database.data.dao.UserDao;
import com.example.virtualcoach.database.di.DataModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.testing.TestInstallIn;

@Module
@TestInstallIn(
        components = SingletonComponent.class,
        replaces = DataModule.Database.class
)
public class TestDatabaseModule {
    @Provides
    @Singleton
    AppDatabase provideDatabase(@ApplicationContext Context appContext) {
        //ApplicationProvider.getApplicationContext()
        return Room.inMemoryDatabaseBuilder(appContext, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
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
}