package com.example.virtualcoach.database.data.repository;

import androidx.annotation.NonNull;

import com.example.virtualcoach.app.di.ApplicationModule.Threads.DBExecutor;
import com.example.virtualcoach.app.preferences.AppPreferences;
import com.example.virtualcoach.database.data.model.User;
import com.example.virtualcoach.database.data.source.UserDataSource;
import com.example.virtualcoach.usermanagement.model.DisplayUser;
import com.example.virtualcoach.usermanagement.modules.login.LoginResult;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.example.virtualcoach.app.util.NullUtils.isNull;

@Singleton
public class UserRepository {

    private final UserDataSource dataSource;
    private final Executor executor;
    private final AppPreferences preferences;

    private DisplayUser currentUser;

    @Inject
    public UserRepository(UserDataSource dataSource
            , @DBExecutor Executor executor
            , AppPreferences preferences) {
        this.dataSource = dataSource;
        this.executor = executor;
        this.preferences = preferences;
    }

    public ListenableFuture<DisplayUser> register(String email, String password) {
        SettableFuture<DisplayUser> future = SettableFuture.create();

        executor.execute(() -> {
            try {
                future.set(dataSource.register(email, password));

            } catch (Exception ex) {
                future.setException(ex);
            }
        });

        return future;
    }

    public ListenableFuture<LoginResult> login(String username, String password) {
        SettableFuture<LoginResult> future = SettableFuture.create();

        executor.execute(() -> {
            try {
                DisplayUser user = dataSource.login(username, password);
                future.set(new LoginResult(user));

            } catch (Exception e) {
                future.setException(e);
            }
        });
        return future;
    }

    public void setCurrentUser(@NonNull DisplayUser user) {
        updateCurrentUserId(user);
        currentUser = null;
    }

    @NonNull
    public DisplayUser getCurrentUser() {
        if (isNull(currentUser))
            updateCurrentUser();

        return currentUser;
    }

    private void updateCurrentUser() {
        try {
            currentUser = dataSource.getById(loadCurrentUserId()).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String loadCurrentUserId() {
        return preferences.get(AppPreferences.CURRENT_USER_ID);
    }

    private void updateCurrentUserId(@NonNull DisplayUser user) {
        preferences.set(AppPreferences.CURRENT_USER_ID, user.id);
    }

    public boolean isLoggedIn() {
        return !isNull(loadCurrentUserId());
    }

    public void logout() {
        preferences.remove(AppPreferences.CURRENT_USER_ID);
        currentUser = null;
    }

    public User getByEmail(String email) {
        return dataSource.getByEmail(email);
    }

    public void update(DisplayUser currentUser) {
        dataSource.update(currentUser);
    }

}
