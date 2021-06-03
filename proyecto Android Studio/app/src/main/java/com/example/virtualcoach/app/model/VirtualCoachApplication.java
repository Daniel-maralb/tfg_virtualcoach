package com.example.virtualcoach.app.model;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.preference.PreferenceManager;
import androidx.work.Configuration;

import com.example.virtualcoach.R;
import com.example.virtualcoach.app.preferences.AppPreferences;
import com.example.virtualcoach.app.worker.DBImportWorker;
import com.example.virtualcoach.database.data.repository.UserRepository;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

import static com.example.virtualcoach.app.util.NullUtils.isNull;

@HiltAndroidApp
public class VirtualCoachApplication extends Application implements Configuration.Provider {
    private static final Duration MIN_INTERVAL_BETWEEN_IMPORTS = Duration.ofDays(1);

    @Inject
    HiltWorkerFactory workerFactory;
    @Inject
    AppPreferences preferences;
    @Inject
    UserRepository userRepository;

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);

        maybeImportData();
    }

    private void maybeImportData() {

        if (!userRepository.isLoggedIn())
            return;

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permisos de almacenamiento requeridos para importar", Toast.LENGTH_LONG).show();
            return;
        }

        String dbToImport = preferences.get(AppPreferences.DB_TO_IMPORT);
        if (TextUtils.isEmpty(dbToImport) || !new File(dbToImport).canRead()) {
            Toast.makeText(this, "Por favor, configure el camino a la base de datos", Toast.LENGTH_LONG).show();
            return;
        }

        String lastImport_str = preferences.get(AppPreferences.LAST_IMPORT);
        if (isNull(lastImport_str)) {
            importData();
            return;
        }

        LocalDateTime lastImport = LocalDateTime.parse(lastImport_str);
        LocalDateTime now = LocalDateTime.now();

        if (lastImport.plus(MIN_INTERVAL_BETWEEN_IMPORTS).isBefore(now))
            importData();
    }

    private void importData() {
        DBImportWorker.enqueueOneTimeWithArgs(this, true);
    }
}
