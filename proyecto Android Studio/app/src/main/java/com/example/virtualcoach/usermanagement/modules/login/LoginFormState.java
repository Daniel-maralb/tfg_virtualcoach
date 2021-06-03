package com.example.virtualcoach.usermanagement.modules.login;

import androidx.annotation.Nullable;

import static com.example.virtualcoach.app.util.NullUtils.isNull;

/**
 * Data validation state of the login form.
 */
class LoginFormState {
    @Nullable
    private final Integer usernameError;
    @Nullable
    private final Integer passwordError;
    @Nullable
    private final Integer serverError;

    LoginFormState(@Nullable Integer usernameError,
                   @Nullable Integer passwordError,
                   @Nullable Integer otherError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.serverError = otherError;
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
    public Integer getOtherError() {
        return serverError;
    }

    boolean isDataValid() {
        return isNull(usernameError) && isNull(passwordError) && isNull(serverError);
    }
}