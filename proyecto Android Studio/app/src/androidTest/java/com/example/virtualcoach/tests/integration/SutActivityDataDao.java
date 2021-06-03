package com.example.virtualcoach.tests.integration;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.example.virtualcoach.database.data.AppDatabase;
import com.example.virtualcoach.database.data.dao.ActivityDataDao;
import com.example.virtualcoach.database.data.model.ActivityData;
import com.example.virtualcoach.util.LiveDataTestUtil;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.virtualcoach.app.util.NullUtils.isNull;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Recommended to run on device.
 */
@RunWith(Enclosed.class)
public class SutActivityDataDao {

    @RunWith(Parameterized.class)
    public static class insertAllTests extends SutActivityDataDao {

        @Parameterized.Parameter()
        public String inputsDescription;
        @Parameterized.Parameter(1)
        public List<ActivityData> dataToImport;
        @Parameterized.Parameter(2)
        public String resultsDescription;
        @Parameterized.Parameter(3)
        public List<Matcher<Long>> expectedInsertResults;
        @Parameterized.Parameter(4)
        public List<ActivityData> expectedData;
        @Parameterized.Parameter(5)
        public Class<? extends Exception> expectedException;

        @Parameterized.Parameters(name = "{index}: {0} -> {2}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {
                            "Null data",
                            null,
                            "Exception (ExecutionException)",
                            List.of(),
                            List.of(),
                            ExecutionException.class
                    },
                    {
                            "Input is empty",
                            Collections.emptyList(),
                            "Database is not modified",
                            Collections.emptyList(),
                            initialState,
                            null
                    },
                    {
                            "New data",
                            List.of(
                                    getCorrect(4),
                                    getCorrect(5)
                            ),
                            "Data is inserted",
                            List.of(not(-1L), not(-1L)),
                            addToList(initialState, getCorrect(4), getCorrect(5)),
                            null
                    },
                    {
                            "Repeated data",
                            List.of(
                                    getRepeated(1),
                                    getRepeated(2)
                            ),
                            "Existing data is kept",
                            List.of(is(-1L), is(-1L)),
                            initialState,
                            null
                    },
                    {
                            "New and repeated data",
                            List.of(
                                    getRepeated(2),
                                    getRepeated(3),
                                    getCorrect(4),
                                    getCorrect(5)
                            ),
                            "Existing data is kept and new data is inserted",
                            List.of(is(-1L), is(-1L), not(-1L), not(-1L)),
                            addToList(initialState, getCorrect(4), getCorrect(5)),
                            null
                    },
            });
        }

        @Test
        public void runTests() {
            //Given a initial state and a set of data

            try {
                //When that data is inserted in the database
                List<Long> insertResult = activityDataDao.insertAll(dataToImport).get();

                //Then
                //  It may fail
                if (!isNull(expectedException))
                    fail("Exception expected: " + expectedException);

                //  The individual inserts succeed or fail
                for (int i = 0; i < insertResult.size(); i++)
                    assertThat(insertResult.get(i), expectedInsertResults.get(i));

                //  And the inserted data is recoverable
                List<ActivityData> resultGet = LiveDataTestUtil.getValue(activityDataDao.getAll());

                assertThat(resultGet, equalTo(expectedData));
            } catch (Exception e) {
                assertThat(e, instanceOf(expectedException));
            }
        }
    }

    static final List<ActivityData> initialState = List.of(
            getCorrect(1),
            getCorrect(2),
            getCorrect(3)
    );

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        activityDataDao = db.activityDataDao();
        activityDataDao.insertAll(initialState);
    }

    @After
    public void tearDown() {
        db.close();
    }

    ActivityDataDao activityDataDao;
    AppDatabase db;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private static List<ActivityData> addToList(List<ActivityData> list, ActivityData... newData) {
        List<ActivityData> newList = new ArrayList<>(list);
        newList.addAll(Arrays.asList(newData));

        newList.sort((o1, o2) -> o1.timestamp - o2.timestamp);

        return newList;
    }

    public static ActivityData getCorrect(int id) {
        return new ActivityData(10 * id, id, id);
    }

    public static ActivityData getRepeated(int id) {
        return new ActivityData(10 * id, id + 1, id + 1);
    }
}

