package com.example.virtualcoach.app.ui;

import android.Manifest;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.virtualcoach.R;
import com.example.virtualcoach.app.di.ApplicationModule.Threads.DBExecutor;
import com.example.virtualcoach.app.preferences.AppPreferences;
import com.example.virtualcoach.app.util.PermissionGuard;
import com.example.virtualcoach.app.worker.DBImportWorker;
import com.example.virtualcoach.database.data.repository.UserRepository;
import com.example.virtualcoach.sessionlog.UpdateSessionLogsTask;
import com.example.virtualcoach.usermanagement.model.DisplayUser;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.example.virtualcoach.app.util.NullUtils.isNull;

@AndroidEntryPoint
public class PreferencesFragment extends PreferenceFragmentCompat {

    @Inject
    UserRepository userRepository;
    @Inject
    AppPreferences appPreferences;
    @Inject
    UpdateSessionLogsTask updateSessionLogsTask;
    @Inject
    @DBExecutor
    Executor dbExecutor;

    private PermissionGuard storageAccess;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storageAccess = new PermissionGuard(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                R.string.permission_storage_rationale);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        initDBPreferences();
        initUserPreferences();
    }

    private void initUserPreferences() {

        final Preference logout = findPreference(getString(R.string.preference_logout_button));
        logout.setOnPreferenceClickListener(preference -> {
            userRepository.logout();
            NavHostFragment.findNavController(this).navigate(PreferencesFragmentDirections.actionSettingsFragmentToLoginFragment());
            return true;
        });

        DisplayUser currentUser = userRepository.getCurrentUser();

        final EditTextPreference userAge = findPreference("preference_user_age");
        userAge.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        });

        userAge.setText(isNull(currentUser.age) ?
                ""
                : String.valueOf(currentUser.age));

        userAge.setOnPreferenceChangeListener((preference, newValue) -> {
            try {
                int value = Integer.parseInt(newValue.toString());
                currentUser.age = value;
                userRepository.update(currentUser);

                userAge.setText(Integer.toString(value));

            } catch (NumberFormatException e) {
                notifyGenericError();
            }
            return false;
        });


        final EditTextPreference userWeight = findPreference("preference_user_weight");
        userWeight.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        });

        userWeight.setText(isNull(currentUser.weight) ?
                ""
                : String.valueOf(currentUser.weight));

        userWeight.setOnPreferenceChangeListener((preference, newValue) -> {
            try {
                int value = Integer.parseInt(newValue.toString());
                currentUser.weight = value;
                userRepository.update(currentUser);

                userWeight.setText(Integer.toString(value));

            } catch (NumberFormatException e) {
                notifyGenericError();
            }
            return false;
        });


        final EditTextPreference userHeight = findPreference("preference_user_height");
        userHeight.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        });

        userHeight.setText(isNull(currentUser.height) ?
                ""
                : String.valueOf(currentUser.height));

        userHeight.setOnPreferenceChangeListener((preference, newValue) -> {
            try {
                int value = Integer.parseInt(newValue.toString());
                currentUser.height = value;
                userRepository.update(currentUser);

                userHeight.setText(Integer.toString(value));

            } catch (NumberFormatException e) {
                notifyGenericError();
            }
            return false;
        });
    }

    private void initDBPreferences() {
        final Preference refreshDB = findPreference(getString(R.string.preference_refreshdb_button));
        refreshDB.setOnPreferenceClickListener(preference -> {
            storageAccess.request(() -> DBImportWorker.enqueueOneTime(getContext()));
            return true;
        });

        final EditTextPreference gbdb = findPreference("db_to_import");
        gbdb.setOnPreferenceChangeListener((preference, newValue) -> {
            if (((String) newValue).isEmpty()) {
                appPreferences
                        .set(AppPreferences.DB_TO_IMPORT, AppPreferences.DB_TO_IMPORT.defaultValue);
                gbdb.setText(AppPreferences.DB_TO_IMPORT.defaultValue);
                return false;
            }
            return true;
        });
    }

    private void notifyGenericError() {
        Toast.makeText(getContext(), "Ha ocurrido un error!", Toast.LENGTH_LONG).show();
    }
}