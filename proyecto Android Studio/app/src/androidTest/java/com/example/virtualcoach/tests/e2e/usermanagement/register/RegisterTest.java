package com.example.virtualcoach.tests.e2e.usermanagement.register;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;

import com.example.virtualcoach.app.ui.MainActivity;
import com.example.virtualcoach.database.data.model.User;
import com.example.virtualcoach.database.data.repository.UserRepository;
import com.example.virtualcoach.usermanagement.di.UserManagementModule.PasswordSecurities.SHASecurity;
import com.example.virtualcoach.usermanagement.model.security.PasswordSecurity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static com.example.virtualcoach.app.util.NullUtils.isNull;
import static com.example.virtualcoach.util.TestData.EMAIL_EMPTY;
import static com.example.virtualcoach.util.TestData.EMAIL_MALFORMED;
import static com.example.virtualcoach.util.TestData.EMAIL_VALID_1;
import static com.example.virtualcoach.util.TestData.EMAIL_VALID_2;
import static com.example.virtualcoach.util.TestData.MSG_EMAIL_INVALID;
import static com.example.virtualcoach.util.TestData.MSG_EMAIL_IN_USE;
import static com.example.virtualcoach.util.TestData.MSG_PASSWORD_INVALID;
import static com.example.virtualcoach.util.TestData.MSG_PASSWORD_INVALID_LENGTH;
import static com.example.virtualcoach.util.TestData.MSG_PASSWORD_REPEAT_MATCH;
import static com.example.virtualcoach.util.TestData.PASSWORD_EMPTY;
import static com.example.virtualcoach.util.TestData.PASSWORD_INVALID_LESS_THAN_8;
import static com.example.virtualcoach.util.TestData.PASSWORD_INVALID_MORE_THAN_32;
import static com.example.virtualcoach.util.TestData.PASSWORD_VALID_1;
import static com.example.virtualcoach.util.TestResources.getString;
import static com.example.virtualcoach.util.TestResources.waitForTransition;
import static org.junit.Assert.fail;

@HiltAndroidTest
@LargeTest
public class RegisterTest {
    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Rule(order = 1)
    public ActivityScenarioRule<MainActivity> mainActivityRule = new ActivityScenarioRule<>(MainActivity.class);
    @Rule(order = 2)
    public InstantTaskExecutorRule taskExecutorRule = new InstantTaskExecutorRule();

    @Inject
    RegisterSteps steps;
    @Inject
    UserRepository userRepository;
    @Inject
    @SHASecurity
    PasswordSecurity passwordSecurity;

    @Before
    public void setUp() {
        hiltRule.inject();

        userRepository.register(EMAIL_VALID_2, PASSWORD_VALID_1);

        // All cases start on the register fragment
        steps.navigateToRegisterPage();
    }

    private String email;
    private String password;
    private String passwordRepeat;

    private String errorMsg;

    @Test
    public void E2_error_email_empty() {
        email = EMAIL_EMPTY;
        errorMsg = getString(MSG_EMAIL_INVALID);

        //Given

        //When
        steps.enterEmail(email);
        //Then
        steps.checkEmailError(errorMsg);
        steps.checkRegisterDisabled();
    }

    @Test
    public void E3_error_email_malformed() {
        email = EMAIL_MALFORMED;
        errorMsg = getString(MSG_EMAIL_INVALID);

        //Given

        //When
        steps.enterEmail(email);
        //Then
        steps.checkEmailError(errorMsg);
    }

    @Test
    public void E4_error_email_exists() {
        email = EMAIL_VALID_2;
        password = PASSWORD_VALID_1;
        passwordRepeat = password;
        errorMsg = getString(MSG_EMAIL_IN_USE);

        //Given
        steps.enterEmail(email);
        steps.enterPassword(password);
        steps.enterRepeatedPassword(passwordRepeat);

        //When
        steps.clickRegister();

        //Then
        steps.checkEmailError(errorMsg);
        steps.checkRegisterDisabled();
    }

    @Test
    public void E5_error_password_empty() {
        password = PASSWORD_EMPTY;
        errorMsg = getString(MSG_PASSWORD_INVALID);

        //Given

        //When
        steps.enterPassword(password);
        //Then
        steps.checkPasswordError(errorMsg);
    }

    @Test
    public void E6_error_password_invalid_more32() {
        password = PASSWORD_INVALID_MORE_THAN_32;
        errorMsg = getString(MSG_PASSWORD_INVALID_LENGTH);

        //Given

        //When
        steps.enterPassword(password);
        //Then
        steps.checkPasswordError(errorMsg);
        steps.checkRegisterDisabled();
    }

    @Test
    public void E6_error_password_invalid_less8() {
        password = PASSWORD_INVALID_LESS_THAN_8;
        errorMsg = getString(MSG_PASSWORD_INVALID_LENGTH);

        //Given

        //When
        steps.enterPassword(password);
        //Then
        steps.checkPasswordError(errorMsg);
        steps.checkRegisterDisabled();
    }

    @Test
    public void E7_error_repeat_password_does_not_match() {
        password = PASSWORD_VALID_1;
        passwordRepeat = PASSWORD_EMPTY;
        errorMsg = getString(MSG_PASSWORD_REPEAT_MATCH);

        //Given

        //When
        steps.enterPassword(password);
        steps.enterRepeatedPassword(passwordRepeat);
        //Then
        steps.checkRepeatPasswordError(errorMsg);
        steps.checkRegisterDisabled();
    }

    @Test
    public void E1_correct_registry_button() {
        email = EMAIL_VALID_1;
        password = PASSWORD_VALID_1;
        passwordRepeat = password;

        //Given
        steps.enterEmail(email);
        steps.enterPassword(password);
        steps.enterRepeatedPassword(passwordRepeat);
        //When
        steps.clickRegister();
        //Then
        steps.checkInLogin();
        checkUserInDB(email, password);
    }

    @Test
    public void E1_correct_registry_keyboard() {
        email = EMAIL_VALID_1;
        password = PASSWORD_VALID_1;
        passwordRepeat = password;

        //Given
        steps.enterEmail(email);
        steps.enterPassword(password);
        steps.enterRepeatedPassword(passwordRepeat);

        //When
        steps.sendWithKeyboard();

        //Then
        waitForTransition();

        steps.checkInLogin();
        checkUserInDB(email, password);
    }

    @Test
    public void E8_check_errors_keyboard_email() {
        email = EMAIL_MALFORMED;

        //Given
        steps.enterEmail(email);
        //When
        steps.sendWithKeyboard();
        //Then
        steps.checkEmailError(getString(MSG_EMAIL_INVALID));
    }

    @Test
    public void E8_check_errors_keyboard_password() {
        password = PASSWORD_INVALID_LESS_THAN_8;

        //Given
        steps.enterPassword(password);
        //When
        steps.sendWithKeyboard();
        //Then
        steps.checkPasswordError(getString(MSG_PASSWORD_INVALID_LENGTH));
    }

    @Test
    public void E8_check_errors_keyboard_passwordRepeat() {
        passwordRepeat = PASSWORD_INVALID_LESS_THAN_8;

        //Given
        steps.enterRepeatedPassword(passwordRepeat);
        //When
        steps.sendWithKeyboard();
        //Then
        steps.checkRepeatPasswordError(getString(MSG_PASSWORD_REPEAT_MATCH));
    }

    @Test
    public void E8_check_errors_keyboard_allErrors() {
        email = EMAIL_MALFORMED;
        password = PASSWORD_INVALID_LESS_THAN_8;
        passwordRepeat = PASSWORD_EMPTY;

        //Given
        steps.enterEmail(email);
        steps.enterPassword(password);
        steps.enterRepeatedPassword(passwordRepeat);

        //When
        steps.sendWithKeyboard();

        //Then
        steps.checkEmailError(getString(MSG_EMAIL_INVALID));
        steps.checkPasswordError(getString(MSG_PASSWORD_INVALID_LENGTH));
        steps.checkRepeatPasswordError(getString(MSG_PASSWORD_REPEAT_MATCH));
    }

    @Test
    public void E8_check_errors_keyboard_empty() {

        //When
        steps.sendWithKeyboard();

        //Then
        steps.checkEmailError(getString(MSG_EMAIL_INVALID));
        steps.checkPasswordError(getString(MSG_PASSWORD_INVALID));
    }

    private void checkUserInDB(String email, String password) {
        User user = userRepository.getByEmail(email);

        if (isNull(user))
            fail("No user found");

        passwordSecurity.matches(password, user.password);
    }

}
