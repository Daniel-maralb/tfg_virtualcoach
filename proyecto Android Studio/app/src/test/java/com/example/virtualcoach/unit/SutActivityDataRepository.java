package com.example.virtualcoach.unit;

import com.example.virtualcoach.database.data.model.ActivityData;
import com.example.virtualcoach.database.data.repository.ActivityDataRepository;
import com.example.virtualcoach.database.data.repository.DefaultActivityDataRepository;
import com.example.virtualcoach.database.data.source.FakeActivityDataSource;
import com.example.virtualcoach.database.data.source.FakeImportableActivityDataSource;
import com.example.virtualcoach.database.data.source.ImportableActivityDataSource;
import com.google.common.util.concurrent.ListenableFuture;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.virtualcoach.app.util.NullUtils.isNull;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(Enclosed.class)
public class SutActivityDataRepository {

    @RunWith(Parameterized.class)
    public static class importActivityDataFromTests extends SutActivityDataRepository {

        @Parameterized.Parameter()
        public String inputsDescription;
        @Parameterized.Parameter(1)
        public ImportableActivityDataSource importableDataSource;
        @Parameterized.Parameter(2)
        public String resultsDescription;
        @Parameterized.Parameter(3)
        public List<ActivityData> expectedData;
        @Parameterized.Parameter(4)
        public Class<? extends Exception> expectedException;

        @Parameterized.Parameters(name = "{index}: {0} -> {2}")
        public static Iterable<Object[]> importGBDB() {
            return Arrays.asList(new Object[][]{
                    {
                            "Input is null",
                            null,
                            "Exception NPE",
                            initialDB,
                            NullPointerException.class,
                    },
                    {
                            "Input is empty",
                            new FakeImportableActivityDataSource(getDatabaseFrom()),
                            "Database is not modified",
                            initialDB,
                            null,
                    },
                    {
                            "One window of data",
                            new FakeImportableActivityDataSource(2, getDatabaseFrom(
                                    getCorrect(4),
                                    getCorrect(5)
                            )),
                            "Data is imported successfully",
                            addToDatabase(initialDB,
                                    getCorrect(4),
                                    getCorrect(5)),
                            null
                    },
                    {
                            "Multiple windows of data",
                            new FakeImportableActivityDataSource(1, getDatabaseFrom(
                                    getCorrect(4),
                                    getCorrect(5),
                                    getCorrect(6)
                            )),
                            "Data is imported successfully",
                            addToDatabase(initialDB,
                                    getCorrect(4),
                                    getCorrect(5),
                                    getCorrect(6)),
                            null
                    }
            });
        }

        @Test
        public void runTest() {
            //Given a initial state and an importable datasource

            try {
                //When the data is imported
//                waitForImport(repository.insertAll(importableDataSource));

                //Then
                //  It may fail
                if (!isNull(expectedException))
                    fail("Exception expected: " + expectedException);

                //  And the newly imported data is recoverable
                List<ActivityData> resultData = localDataSource.getData();

                assertThat(resultData, equalTo(expectedData));

            } catch (Exception e) {
                assertThat(e, instanceOf(expectedException));
            }
        }
    }

    @Before
    public void setUp() {
        localDataSource = new FakeActivityDataSource(initialDB);

        repository = new DefaultActivityDataRepository(localDataSource);
    }

    ActivityDataRepository repository;
    FakeActivityDataSource localDataSource;

    static final List<ActivityData> initialDB = getDatabaseFrom(
            getCorrect(1),
            getCorrect(2),
            getCorrect(3)
    );

    private static void waitForImport(List<ListenableFuture<List<Long>>> importResult) throws ExecutionException, InterruptedException {
        assert importResult != null;

        for (ListenableFuture<List<Long>> future : importResult) {
            future.get();
        }
    }

    private static List<ActivityData> addToDatabase(List<ActivityData> list, ActivityData... newData) {
        List<ActivityData> newList = new ArrayList<>(list);
        newList.addAll(Arrays.asList(newData));

        newList.sort((o1, o2) -> o1.timestamp - o2.timestamp);

        return newList;
    }

    private static ActivityData getCorrect(int id) {
        return new ActivityData(10 * id, id, id);
    }

    private static List<ActivityData> getDatabaseFrom(ActivityData... data) {
        List<ActivityData> list = new ArrayList<>(Arrays.asList(data));

        list.sort((o1, o2) -> o1.timestamp - o2.timestamp);

        return list;
    }
}