package com.example.virtualcoach.tests.e2e.usermanagement.register;

import androidx.annotation.StringRes;

import com.example.virtualcoach.R;

import javax.inject.Inject;
import javax.inject.Singleton;

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
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@Singleton
public class RegisterRobot {

    @Inject
    RegisterRobot() {
    }

    public void navigateToRegister() {
        onView(withId(R.id.fragment_login)).check(matches(isDisplayed()));
        onView(withId(R.id.login_btn_register)).perform(click());
        onView(withId(R.id.fragment_register)).check(matches(isDisplayed()));
    }

    public void enterEmail(String email) {
        if (email.isEmpty())
            onView(withId(R.id.register_username)).perform(typeText("aaa"), clearText());
        onView(withId(R.id.register_username)).perform(typeText(email));
        closeSoftKeyboard();
    }

    public void enterPassword(String password) {
        if (password.isEmpty())
            onView(withId(R.id.register_password)).perform(typeText("aaa"), clearText());
        onView(withId(R.id.register_password)).perform(typeText(password));
        closeSoftKeyboard();
    }

    public void enterPasswordRepeat(String password) {
        onView(withId(R.id.register_repeat_password)).perform(typeText(password));
        closeSoftKeyboard();
    }

    public void clickRegister() {
        onView(withId(R.id.register_btn_register)).perform(click());
    }

    public void assertResultMsgEquals(@StringRes Integer msgId) {
        onView(withId(R.id.register_result)).check(matches(withText(msgId)));
        onView(withId(R.id.register_result)).check(matches(isDisplayed()));
    }

    public void assertUsernameError(String errorMsg) {
        onView(withId(R.id.register_username)).check(matches(hasErrorText(errorMsg)));
    }

    public void assertPasswordError(String errorMsg) {
        onView(withId(R.id.register_password)).check(matches(hasErrorText(errorMsg)));
    }

    public void assertRepeatPasswordError(String errorMsg) {
        onView(withId(R.id.register_repeat_password)).check(matches(hasErrorText(errorMsg)));
    }

    public void assertInLogin() {
        onView(withId(R.id.fragment_login)).check(matches(isDisplayed()));
    }

    public void sendWithKeyboard() {
        onView(withId(R.id.register_repeat_password)).perform(pressImeActionButton());
    }

    public void assertRegisterDisabled() {
        onView(withId(R.id.register_btn_register)).check(matches(not(isEnabled())));
    }
}
