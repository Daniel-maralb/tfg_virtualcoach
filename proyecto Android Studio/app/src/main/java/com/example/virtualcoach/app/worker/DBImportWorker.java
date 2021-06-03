package com.example.virtualcoach.app.worker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.hilt.work.HiltWorker;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.ForegroundInfo;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.virtualcoach.R;
import com.example.virtualcoach.app.preferences.AppPreferences;
import com.example.virtualcoach.database.data.model.ActivityData;
import com.example.virtualcoach.database.data.repository.ActivityDataRepository;
import com.example.virtualcoach.database.data.source.ImportableActivityDataSource;
import com.example.virtualcoach.sessionlog.UpdateSessionLogsTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

import static com.example.virtualcoach.app.util.NullUtils.isNull;
import static com.example.virtualcoach.database.data.source.GBExportDataSource.COL_HR;
import static com.example.virtualcoach.database.data.source.GBExportDataSource.COL_STEPS;
import static com.example.virtualcoach.database.data.source.GBExportDataSource.COL_TIMESTAMP;

@HiltWorker
public class DBImportWorker extends Worker {
    private static final Logger LOG = LoggerFactory.getLogger(DBImportWorker.class);

    private static final String ARG_FAST_IMPORT = "fast_import";
    private static final String UNIQUE_NAME = "DBImportWorker";
    private static final String NOTIFICATION_CHANNEL_ID = "virtualcoach";

    //Used in-app for updating or removing notification
    private static final int NOTIFICATION_ID = 1;
    private long imported;

    public static void enqueueOneTimeWithArgs(Context context, boolean fastImport) {
        Data arguments = new Data.Builder()
                .putBoolean(ARG_FAST_IMPORT, fastImport)
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(DBImportWorker.class)
                .setInputData(arguments)
                .build();

        WorkManager workManager = WorkManager.getInstance(context.getApplicationContext());

        workManager.enqueueUniqueWork(
                UNIQUE_NAME,
                ExistingWorkPolicy.KEEP,
                workRequest);
    }

    public static void enqueueOneTime(Context context) {
        OneTimeWorkRequest workRequest = OneTimeWorkRequest.from(DBImportWorker.class);
        WorkManager workManager = WorkManager.getInstance(context.getApplicationContext());

        workManager.enqueueUniqueWork(
                UNIQUE_NAME,
                ExistingWorkPolicy.KEEP,
                workRequest);
    }


    private final ActivityDataRepository repository;
    private final ImportableActivityDataSource dataSourceToImport;

    private final UpdateSessionLogsTask updateSessionLogsTask;
    private final AppPreferences preferences;

    private final boolean fastImport;

    @AssistedInject
    public DBImportWorker(
            @Assisted @NonNull Context appContext,
            @Assisted @NonNull WorkerParameters workerParams,
            ActivityDataRepository repository,
            ImportableActivityDataSource dataSource,
            UpdateSessionLogsTask updateSessionLogsTask,
            AppPreferences preferences) {
        super(appContext, workerParams);
        this.repository = repository;
        this.dataSourceToImport = dataSource;
        this.updateSessionLogsTask = updateSessionLogsTask;
        this.preferences = preferences;
        this.fastImport = workerParams.getInputData().getBoolean(ARG_FAST_IMPORT, false);
    }

    @NonNull
    @Override
    public Result doWork() {
        // DEPRECATED setForegroundAsync in Android 12 or WorkManager 2.7.0-alpha
        Future<Void> future = setForegroundAsync(createForegroundInfo(getApplicationContext(), null));

        if (fastImport) {
            String lastImport_str = preferences.get(AppPreferences.LAST_IMPORT);

            if (!isNull(lastImport_str)) {
                LOG.debug("Fast import is set, starting at " + lastImport_str);
                dataSourceToImport.startAt(LocalDateTime.parse(lastImport_str));
            }
        }

        LOG.debug("Importing data...");
        importDBSync();

        LOG.debug("Data import finished, updating session logs");
        updateSessionLogs();

        LOG.debug("Session logs updated, updating last modification time");
        updateImportDate();

        LOG.debug("Last import date updated, finishing");

        // Call to setForegroundAsync must complete before returning
        try {
            future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return ListenableWorker.Result.success();
    }

    private void updateImportDate() {
        preferences.set(AppPreferences.LAST_IMPORT, LocalDateTime.now().toString());
    }

    private void updateSessionLogs() {
        updateSessionLogsTask.run();
    }

    private void importDBSync() {
        long size = dataSourceToImport.getSize();
        LOG.debug(size + " data to import");
        imported = 0;

        ArrayList<Future<Void>> futures = new ArrayList<>();

        while (dataSourceToImport.hasMore()) {
            futures.add(setForegroundAsync(createForegroundInfo(getApplicationContext(), String.format(Locale.US, "Importado: %d%%", (imported * 100) / size))));

            dataSourceToImport.importData(this::importData);
        }

        futures.add(setForegroundAsync(createForegroundInfo(getApplicationContext(), String.format(Locale.US, "Importado: %d%%", (imported * 100) / size))));

        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void importData(Cursor cursor) {

        final int index_HR = cursor.getColumnIndexOrThrow(COL_HR);
        final int index_TIMESTAMP = cursor.getColumnIndexOrThrow(COL_TIMESTAMP);
        final int index_STEPS = cursor.getColumnIndexOrThrow(COL_STEPS);

        int timestamp, hr, steps;

        final int cursorSize = cursor.getCount();

        ArrayList<ActivityData> data = new ArrayList<>(cursorSize);

        LOG.debug("Parsing data of size " + cursorSize);

        while (cursor.moveToNext()) {
            hr = cursor.getInt(index_HR);
            timestamp = cursor.getInt(index_TIMESTAMP);
            steps = cursor.getInt(index_STEPS);

            data.add(new ActivityData(timestamp, hr, steps));
        }

        LOG.debug(data.size() + " parsed from " + data.get(0).getTimestamp() + " to " + data.get(data.size() - 1).getTimestamp());

        repository.insertAllSync(data);

        LOG.debug("Chunk imported");

        imported += cursorSize;
    }

    private static Notification createNotification(Context context, String progress) {
        String title = context.getString(R.string.notification_title);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context,
                NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setTicker(title)
                .setSmallIcon(R.drawable.ic_baseline_autorenew_24)
                .setOnlyAlertOnce(true)
                .setOngoing(true);

        if (!isNull(progress))
            builder.setContentText(progress);

        return builder.build();
    }

    private static ForegroundInfo createForegroundInfo(Context context, String progress) {

        Context applicationContext = context.getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(applicationContext);
        }

        Notification notification = createNotification(applicationContext, progress);

        return new ForegroundInfo(NOTIFICATION_ID, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public static void createChannel(Context context) {
        final int NOTIFICATION_CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel(
                context.getString(R.string.notification_channel_id),
                context.getString(R.string.notification_channel_name),
                NOTIFICATION_CHANNEL_IMPORTANCE);

        channel.setDescription(context.getString(R.string.notification_channel_description));

        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
