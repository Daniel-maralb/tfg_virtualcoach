package com.example.virtualcoach.usermanagement.modules.register;

import android.util.Patterns;

import javax.inject.Inject;

import static com.example.virtualcoach.app.util.NullUtils.isNull;
import static com.example.virtualcoach.app.util.StringUtils.isEmpty;

public class RegisterFormValidator {
    public static class InvalidEmailException extends Exception {
    }

    public static class InvalidPasswordException extends Exception {
    }

    public static class InvalidPasswordLengthException extends Exception {
    }

    public static class InvalidPasswordRepeatException extends Exception {
    }

    @Inject
    RegisterFormValidator() {
    }

    void validateEmail(String email) throws InvalidEmailException {
        if (isEmpty(email) || !isEmailWellFormed(email))
            throw new InvalidEmailException();
    }

    void validatePassword(String password) throws InvalidPasswordException, InvalidPasswordLengthException {
        if (isEmpty(password))
            throw new InvalidPasswordException();

        if (!isPasswordLengthCorrect(password))
            throw new InvalidPasswordLengthException();
    }

    void validatePasswordRepeat(String password, String passwordRepeat) throws InvalidPasswordRepeatException {
        if (isNull(passwordRepeat) || !passwordRepeat.equals(password))
            throw new InvalidPasswordRepeatException();
    }

    private static boolean isPasswordLengthCorrect(String password) {
        return 8 <= password.length() && password.length() <= 32;
    }

    private static boolean isEmailWellFormed(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
