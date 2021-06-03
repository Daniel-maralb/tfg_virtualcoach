package com.example.virtualcoach.usermanagement.modules.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.virtualcoach.R;
import com.example.virtualcoach.app.di.ApplicationModule.Threads.MainExecutor;
import com.example.virtualcoach.database.data.repository.UserRepository;
import com.example.virtualcoach.database.data.source.UserDataSource;
import com.example.virtualcoach.usermanagement.model.DisplayUser;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

import static com.example.virtualcoach.app.util.NullUtils.firstNonNull;

@HiltViewModel
public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    private final Executor executor;
    private final UserRepository loginRepository;
    private final LoginFormValidator validator;

    @Inject
    LoginViewModel(UserRepository loginRepository, LoginFormValidator userDataValidator,
                   @MainExecutor Executor mainExecutor) {
        this.loginRepository = loginRepository;
        this.validator = userDataValidator;
        this.executor = mainExecutor;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        addResultCallbacks(loginRepository.login(username, password));
    }

    public void loginDataChanged(String username, String password) {
        Integer usernameError = null;
        try {
            validator.validateUsername(username);
        } catch (LoginFormValidator.EmptyUsernameException e) {
            usernameError = R.string.invalid_username;
        }

        Integer passwordError = null;
        try {
            validator.validatePassword(password);
        } catch (LoginFormValidator.EmptyPasswordException e) {
            passwordError = R.string.invalid_password;
        }

        loginFormState.setValue(new LoginFormState(usernameError, passwordError, null));
    }

    private void addResultCallbacks(ListenableFuture<LoginResult> future) {
        Futures.addCallback(future, new FutureCallback<LoginResult>() {
            @Override
            public void onSuccess(@NullableDecl LoginResult result) {
                handleLoginSuccess(result.getSuccess());
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
                t.printStackTrace();
                handleLoginError(t);
            }
        }, executor);
    }

    private void handleLoginSuccess(DisplayUser user) {
        loginRepository.setCurrentUser(user);
        loginResult.setValue(new LoginResult(user));
    }

    private void handleLoginError(Throwable t) {
        Integer usernameError = null;
        Integer passwordError = null;
        Integer serverError = null;

        if (t instanceof LoginFormValidator.EmptyUsernameException)
            usernameError = R.string.invalid_username;

        else if (t instanceof UserDataSource.UsernameDoesNotExistException)
            usernameError = R.string.unknown_username;

        else if (t instanceof LoginFormValidator.EmptyPasswordException)
            passwordError = R.string.invalid_password;

        else if (t instanceof UserDataSource.PasswordDoesNotMatchException)
            passwordError = R.string.wrong_password;

        else
            serverError = R.string.login_failed;

        loginFormState.setValue(new LoginFormState(usernameError, passwordError, serverError));

        loginResult.setValue(new LoginResult(firstNonNull(usernameError, passwordError, serverError)));
    }
}