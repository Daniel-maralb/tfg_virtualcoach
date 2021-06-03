package com.example.virtualcoach.usermanagement.modules.register;

import androidx.annotation.Nullable;

import com.example.virtualcoach.usermanagement.model.DisplayUser;

/**
 * Authentication result : success (user details) or error message.
 */
public class RegisterResult {
    @Nullable
    private DisplayUser success;
    @Nullable
    private Integer error;

    public RegisterResult() {
    }

    public RegisterResult(@Nullable Integer error) {
        this.error = error;
    }

    public RegisterResult(@Nullable DisplayUser success) {
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