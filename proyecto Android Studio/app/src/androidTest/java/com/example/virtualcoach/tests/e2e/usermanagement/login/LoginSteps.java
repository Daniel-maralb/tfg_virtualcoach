package com.example.virtualcoach.tests.e2e.usermanagement.login;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LoginSteps {
    LoginRobot robot;

    @Inject
    LoginSteps(LoginRobot robot) {
        this.robot = robot;
    }

    public void navigateToLoginPage() {
        robot.navigateToLogin();
    }

    public void enterPassword(String passwordValid) {
        robot.enterPassword(passwordValid);
    }

    public void enterUsername(String username) {
        robot.enterUsername(username);
    }

    public void clickLogin() {
        robot.clickLogin();
    }

    public void checkInHome() {
        robot.assertInHome();
    }

    public void checkUsernameError(String errorMsg) {
        robot.assertUsernameError(errorMsg);
    }

    public void checkPasswordError(String errorMsg) {
        robot.assertPasswordError(errorMsg);
    }

    public void checkLoginDisabled() {
        robot.assertLoginDisabled();
    }

    public void sendWithKeyboard() {
        robot.sendWithKeyboard();
    }
}
