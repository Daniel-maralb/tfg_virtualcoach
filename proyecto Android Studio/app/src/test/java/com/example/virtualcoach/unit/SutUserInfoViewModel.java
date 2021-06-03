package com.example.virtualcoach.unit;

import android.content.Context;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.work.Configuration;
import androidx.work.WorkerFactory;
import androidx.work.impl.utils.SynchronousExecutor;
import androidx.work.testing.WorkManagerTestInitHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

@RunWith(AndroidJUnit4.class)
public class SutUserInfoViewModel {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock
    WorkerFactory workerFactory;

    Context appContext;

    @Before
    public void setUp() {
        appContext = ApplicationProvider.getApplicationContext();

        Configuration config = new Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .setMinimumLoggingLevel(Log.DEBUG)
                .setExecutor(new SynchronousExecutor())
                .build();

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(appContext, config);
    }

    @Test
    public void importGBDB_launchesWorker() {
//        //Given a fresh viewmodel
//        UserInfoViewModel userInfoViewModel = new UserInfoViewModel();
//
//        //When importing the GB database
//        userInfoViewModel.importGBDatabase(ApplicationProvider.getApplicationContext());
//
//        //Then a worker is launched
//        verify(workerFactory, times(1)).createWorkerWithDefaultFallback(eq(appContext), eq(DBImportWorker.class.getName()), any());
    }
}
