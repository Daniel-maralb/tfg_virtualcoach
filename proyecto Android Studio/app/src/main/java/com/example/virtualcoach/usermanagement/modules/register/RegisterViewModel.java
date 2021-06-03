package com.example.virtualcoach.usermanagement.modules.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.virtualcoach.R;
import com.example.virtualcoach.app.di.ApplicationModule.Threads.MainExecutor;
import com.example.virtualcoach.database.data.repository.UserRepository;
import com.example.virtualcoach.database.data.source.UserDataSource.EmailAlreadyExistsException;
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
public class RegisterViewModel extends ViewModel {
    private final MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private final MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();
    private final UserRepository registerRepository;
    private final RegisterFormValidator validator;
    private final Executor mainThread;

    @Inject
    RegisterViewModel(UserRepository registerRepository,
                      @MainExecutor Executor executor,
                      RegisterFormValidator validator) {
        this.registerRepository = registerRepository;
        this.validator = validator;
        this.mainThread = executor;
    }

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    public void register(String username, String password) {
        // can be launched in a separate asynchronous job
        addRegisterResultCallbacks(registerRepository.register(username, password));
    }

    public void registerDataChanged(String username, String password, String passwordRepeat) {
        registerFormState.setValue(validateFields(username, password, passwordRepeat));
    }

    private void addRegisterResultCallbacks(ListenableFuture<DisplayUser> result) {
        Futures.addCallback(result,
                new FutureCallback<DisplayUser>() {
                    @Override
                    public void onSuccess(@NullableDecl DisplayUser result) {
                        handleRegisterSuccessful(result);
                    }

                    @Override
                    public void onFailure(@NotNull Throwable t) {
                        t.printStackTrace();
                        handleRegisterFailed(t);
                    }
                },
                mainThread);
    }

    private void handleRegisterSuccessful(DisplayUser result) {
        registerResult.setValue(new RegisterResult(result));
    }

    private void handleRegisterFailed(Throwable t) {
        Integer usernameError = null;
        Integer serverError = null;

        if (t instanceof EmailAlreadyExistsException)
            usernameError = R.string.email_in_use;
        else
            serverError = R.string.register_failed;

        registerFormState.setValue(new RegisterFormState(usernameError, null, null, serverError));
        registerResult.setValue(new RegisterResult(firstNonNull(usernameError, serverError)));
    }

    public RegisterFormState validateFields(String username, String password, String passwordRepeat) {
        Integer usernameError = null;
        try {
            validator.validateEmail(username);
        } catch (RegisterFormValidator.InvalidEmailException e) {
            usernameError = R.string.invalid_email;
        }

        Integer passwordError = null;
        try {
            validator.validatePassword(password);
        } catch (RegisterFormValidator.InvalidPasswordException e) {
            passwordError = R.string.invalid_password;
        } catch (RegisterFormValidator.InvalidPasswordLengthException e) {
            passwordError = R.string.invalid_password_length;
        }

        Integer passwordRepeatError = null;
        try {
            validator.validatePasswordRepeat(password, passwordRepeat);
        } catch (RegisterFormValidator.InvalidPasswordRepeatException e) {
            passwordRepeatError = R.string.invalid_password_repeat;
        }

        return new RegisterFormState(usernameError, passwordError, passwordRepeatError, null);
    }
}