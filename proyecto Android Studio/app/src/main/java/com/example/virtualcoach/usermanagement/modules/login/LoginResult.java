package com.example.virtualcoach.usermanagement.modules.login;

import androidx.annotation.Nullable;

import com.example.virtualcoach.usermanagement.model.DisplayUser;

/**
 * Authentication result : success (user details) or error message.
 */
public class LoginResult {
    @Nullable
    private DisplayUser success;
    @Nullable
    private Integer error;

    LoginResult(@Nullable Integer error) {
        this.error = error;
    }

    public LoginResult(@Nullable DisplayUser success) {
        this.success = success;
    }

    @Nullable
    DisplayUser getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}