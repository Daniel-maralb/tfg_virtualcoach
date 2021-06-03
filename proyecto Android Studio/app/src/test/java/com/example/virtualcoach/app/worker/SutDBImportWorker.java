package com.example.virtualcoach.app.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.work.ListenableWorker;
import androidx.work.WorkerFactory;
import androidx.work.WorkerParameters;
import androidx.work.testing.TestWorkerBuilder;

import com.example.virtualcoach.database.data.repository.ActivityDataRepository;
import com.example.virtualcoach.database.data.source.ImportableActivityDataSource;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class SutDBImportWorker {
    private Context context;
    private Executor workerExecutor;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);
    @Mock
    private ActivityDataRepository repository;
    @Mock
    private ImportableActivityDataSource dataSource;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        workerExecutor = Executors.newSingleThreadExecutor();
    }

    @Test
    public void doWork_importSucceeds() {

        SettableFuture<List<Long>> data1 = SettableFuture.create();
        SettableFuture<List<Long>> data2 = SettableFuture.create();
        SettableFuture<List<Long>> data3 = SettableFuture.create();

        List<ListenableFuture<List<Long>>> data = new ArrayList<>(3);
        data.add(data1);
        data.add(data2);
        data.add(data3);

        //Given
        //  A repository that imports data asynchronously
//        given(repository.insertAll(dataSource)).willReturn(data);

        //  Completed async calls
        data1.set(Arrays.asList(1L, 2L, 3L));
        data2.set(Arrays.asList(4L, 5L));
        data3.set(Collections.emptyList());

        //When the worker starts
        DBImportWorker worker =
                (DBImportWorker) TestWorkerBuilder.from(context,
                        DBImportWorker.class,
                        workerExecutor)
                        .setWorkerFactory(new WorkerFactory() {
                            @Nullable
                            @Override
                            public ListenableWorker createWorker(@NonNull Context appContext, @NonNull String workerClassName, @NonNull WorkerParameters workerParameters) {
                                return new DBImportWorker(appContext, workerParameters, repository, dataSource, null);
                            }
                        })
                        .build();

        ListenableWorker.Result result = worker.doWork();

        //Then
        //  The insertion succeeds
        assertThat(result, is(ListenableWorker.Result.success()));
    }
}
