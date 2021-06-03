package com.example.virtualcoach.tests.e2e.usermanagement.login;

import com.example.virtualcoach.R;

import javax.inject.Inject;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

public class LoginRobot {
    @Inject
    LoginRobot() {
    }

    public void navigateToLogin() {
        onView(withId(R.id.fragment_login)).check(matches(isDisplayed()));
    }

    public void enterPassword(String password) {
        if (password.isEmpty())
            onView(withId(R.id.login_password)).perform(typeText("a"), clearText());
        onView(withId(R.id.login_password)).perform(typeText(password));
        closeSoftKeyboard();
    }

    public void enterUsername(String username) {
        if (username.isEmpty())
            onView(withId(R.id.login_username)).perform(typeText("a"), clearText());
        onView(withId(R.id.login_username)).perform(typeText(username));
        closeSoftKeyboard();
    }

    public void clickLogin() {
        onView(withId(R.id.login_btn_login)).check(matches(isEnabled())).perform(click());
    }

    public void assertInHome() {
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()));
    }

    public void assertUsernameError(String errorMsg) {
        onView(withId(R.id.login_username)).check(matches(hasErrorText(errorMsg)));
    }

    public void assertPasswordError(String errorMsg) {
        onView(withId(R.id.login_password)).check(matches(hasErrorText(errorMsg)));
    }

    public void assertLoginDisabled() {
        onView(withId(R.id.login_btn_login)).check(matches(not(isEnabled())));
    }

    public void sendWithKeyboard() {
        onView((withId(R.id.login_password))).perform(click(), pressImeActionButton());
        closeSoftKeyboard();
    }
}
