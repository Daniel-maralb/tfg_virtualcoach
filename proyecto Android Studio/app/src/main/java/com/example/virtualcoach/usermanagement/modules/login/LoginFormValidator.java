package com.example.virtualcoach.usermanagement.modules.login;

import javax.inject.Inject;

import static com.example.virtualcoach.app.util.StringUtils.isEmpty;

public class LoginFormValidator {
    public static class EmptyUsernameException extends Exception {
    }

    public static class EmptyPasswordException extends Exception {
    }

    @Inject
    LoginFormValidator() {
    }

    void validateUsername(String username) throws EmptyUsernameException {
        if (isEmpty(username))
            throw new EmptyUsernameException();
    }

    void validatePassword(String password) throws EmptyPasswordException {
        if (isEmpty(password))
            throw new EmptyPasswordException();
    }
}
