package com.example.virtualcoach.app.di;

import com.example.virtualcoach.app.model.AppExecutors;
import com.example.virtualcoach.app.preferences.AppPreferences;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.Executor;

import javax.inject.Qualifier;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

public class ApplicationModule {

    @Module
    @InstallIn(SingletonComponent.class)
    public static class Threads {
        @Qualifier
        @Retention(RetentionPolicy.RUNTIME)
        public @interface DBExecutor {
        }

        @Qualifier
        @Retention(RetentionPolicy.RUNTIME)
        public @interface MainExecutor {
        }

        @Provides
        @MainExecutor
        Executor getMainExecutor(AppExecutors executors) {
            return executors.mainThread();
        }

        @Provides
        @DBExecutor
        Executor getDBExecutor(AppExecutors executors) {
            return executors.database();
        }
    }


    @Module
    @InstallIn(SingletonComponent.class)
    public static class Singletons {

        @Qualifier
        @Retention(RetentionPolicy.RUNTIME)
        public @interface PathToGBExport {
        }

        @Qualifier
        @Retention(RetentionPolicy.RUNTIME)
        public @interface LastImportTimestamp {
        }

        @Provides
        @PathToGBExport
        String getGBExportPath(AppPreferences preferences) {
            return preferences.get(AppPreferences.DB_TO_IMPORT);
        }

        @Provides
        @LastImportTimestamp
        String getLastImportTimestamp(AppPreferences preferences) {
            return preferences.get(AppPreferences.LAST_IMPORT);
        }
    }
}
