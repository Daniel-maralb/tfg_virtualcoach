package com.example.virtualcoach.usermanagement.modules.register;

import androidx.annotation.Nullable;

import static com.example.virtualcoach.app.util.NullUtils.isNull;

/**
 * Data validation state of the login form.
 */
public class RegisterFormState {
    @Nullable
    private final Integer usernameError;
    @Nullable
    private final Integer passwordError;
    @Nullable
    private final Integer repeatPasswordError;
    @Nullable
    private final Integer serverError;

    RegisterFormState(@Nullable Integer usernameError, @Nullable Integer passwordError, @Nullable Integer repeatPasswordError, @Nullable Integer serverError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.repeatPasswordError = repeatPasswordError;
        this.serverError = serverError;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    Integer getRepeatPasswordError() {
        return repeatPasswordError;
    }

    boolean isDataValid() {
        return isNull(usernameError) && isNull(passwordError) && isNull(repeatPasswordError);
    }

    @Nullable
    public Integer getServerError() {
        return serverError;
    }
}