package com.example.virtualcoach.tests.e2e.usermanagement.login;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ActivityScenario;
import androidx.test.filters.LargeTest;

import com.example.virtualcoach.R;
import com.example.virtualcoach.app.ui.MainActivity;
import com.example.virtualcoach.database.data.model.User;
import com.example.virtualcoach.database.data.repository.UserRepository;
import com.example.virtualcoach.usermanagement.model.DisplayUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static com.example.virtualcoach.util.TestData.EMAIL_EMPTY;
import static com.example.virtualcoach.util.TestData.PASSWORD_EMPTY;
import static com.example.virtualcoach.util.TestData.TEST_USER_1;
import static com.example.virtualcoach.util.TestData.TEST_USER_2;
import static com.example.virtualcoach.util.TestResources.getString;
import static com.example.virtualcoach.util.TestResources.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@HiltAndroidTest
@LargeTest
public class LoginTest {
    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Rule(order = 2)
    public InstantTaskExecutorRule taskExecutorRule = new InstantTaskExecutorRule();

    @Inject
    LoginSteps steps;
    @Inject
    UserRepository userRepository;

    public ActivityScenario<MainActivity> activityScenario;
    private static final User registeredUser = TEST_USER_1;
    private static final User unregisteredUser = TEST_USER_2;

    @Before
    public void setUp() {
        hiltRule.inject();

        addToDB(registeredUser);

        //Given
        // No user is logged in
        userRepository.logout();
        // All cases start on the login fragment
        activityScenario = ActivityScenario.launch(MainActivity.class);
        steps.navigateToLoginPage();
    }

    @After
    public void tearDown() {
        activityScenario.close();
    }

    @Test
    public void E2_username_empty() {
        //When
        steps.enterUsername(EMAIL_EMPTY);

        //Then
        steps.checkUsernameError(getString(R.string.invalid_username));
        steps.checkLoginDisabled();
    }

    @Test
    public void E3_unknown_user_button() {
        //Given
        steps.enterUsername(unregisteredUser.email);
        steps.enterPassword(unregisteredUser.password);

        //When
        steps.clickLogin();

        //Then
        steps.checkUsernameError(getString(R.string.unknown_username));
        steps.checkLoginDisabled();
    }

    @Test
    public void E3_unknown_user_keyboard() {
        //Given
        steps.enterUsername(unregisteredUser.email);
        steps.enterPassword(unregisteredUser.password);

        //When
        steps.sendWithKeyboard();
        sleep(100);

        //Then
        steps.checkUsernameError(getString(R.string.unknown_username));
        steps.checkLoginDisabled();
    }

    @Test
    public void E4_empty_password() {
        //When
        steps.enterPassword(PASSWORD_EMPTY);

        //Then
        steps.checkPasswordError(getString(R.string.invalid_password));
        steps.checkLoginDisabled();
    }

    @Test
    public void E5_wrong_password_button() {
        //Given
        steps.enterUsername(registeredUser.email);
        steps.enterPassword(unregisteredUser.password);

        //When
        steps.clickLogin();

        //Then
        steps.checkPasswordError(getString(R.string.wrong_password));
    }

    @Test
    public void E5_wrong_password_keyboard() {
        //Given
        steps.enterUsername(registeredUser.email);
        steps.enterPassword(unregisteredUser.password);

        //When
        steps.sendWithKeyboard();

        //Then
        steps.checkPasswordError(getString(R.string.wrong_password));
        steps.checkLoginDisabled();
    }

    @Test
    public void E6_empty_form() {
        //When
        steps.sendWithKeyboard();

        //Then
        steps.checkUsernameError(getString(R.string.invalid_username));
        steps.checkPasswordError(getString(R.string.invalid_password));
        steps.checkLoginDisabled();
    }

    @Test
    public void E1_login_correct() {
        //Given
        steps.enterUsername(registeredUser.email);
        steps.enterPassword(registeredUser.password);

        //When
        steps.clickLogin();

        //Then
        checkUserLoggedIn(registeredUser);
        steps.checkInHome();
    }

    private void checkUserLoggedIn(User user) {
        DisplayUser currentUser;

        currentUser = userRepository.getCurrentUser();
        assertThat(currentUser, is(notNullValue()));

        assertThat(currentUser.email, is(user.email));
    }

    private void addToDB(User user) {
        userRepository.register(user.email, user.password);
    }

}
