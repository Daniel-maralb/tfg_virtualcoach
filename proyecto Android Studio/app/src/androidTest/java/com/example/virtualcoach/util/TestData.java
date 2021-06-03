package com.example.virtualcoach.util;

import androidx.annotation.StringRes;

import com.example.virtualcoach.R;
import com.example.virtualcoach.database.data.model.User;

public class TestData {
    public static final String EMAIL_EMPTY = "";
    public static final String EMAIL_MALFORMED = "this is a test";
    public static final String EMAIL_VALID_1 = "test@test.test";
    public static final String EMAIL_VALID_2 = "test2@test.test";

    public static final String PASSWORD_EMPTY = "";
    public static final String PASSWORD_INVALID_LESS_THAN_8 = "7777777";
    public static final String PASSWORD_INVALID_MORE_THAN_32 = "333333333333333333333333333333333";
    public static final String PASSWORD_VALID_1 = "aA,' *.8";
    public static final String PASSWORD_VALID_2 = "88888888";

    public static final User TEST_USER_1 = new User("", EMAIL_VALID_1, PASSWORD_VALID_1);
    public static final User TEST_USER_2 = new User("", EMAIL_VALID_2, PASSWORD_VALID_2);

    @StringRes
    public static final Integer MSG_EMAIL_INVALID = R.string.invalid_email;
    @StringRes
    public static final Integer MSG_EMAIL_IN_USE = R.string.email_in_use;
    @StringRes
    public static final Integer MSG_PASSWORD_INVALID = R.string.invalid_password;
    @StringRes
    public static final Integer MSG_PASSWORD_INVALID_LENGTH = R.string.invalid_password_length;
    @StringRes
    public static final Integer MSG_PASSWORD_REPEAT_MATCH = R.string.invalid_password_repeat;
}
