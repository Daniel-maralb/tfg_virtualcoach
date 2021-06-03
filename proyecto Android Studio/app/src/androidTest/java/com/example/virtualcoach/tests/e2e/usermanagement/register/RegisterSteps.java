package com.example.virtualcoach.tests.e2e.usermanagement.register;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RegisterSteps {

    private final RegisterRobot robot;

    @Inject
    RegisterSteps(RegisterRobot robot) {
        this.robot = robot;
    }


    public void navigateToRegisterPage() {
        robot.navigateToRegister();
    }

    public void enterEmail(String email) {
        robot.enterEmail(email);
    }

    public void enterPassword(String password) {
        robot.enterPassword(password);
    }

    public void enterRepeatedPassword(String password) {
        robot.enterPasswordRepeat(password);
    }

    public void clickRegister() {
        robot.clickRegister();
    }

    public void checkEmailError(String errorMsg) {
        robot.assertUsernameError(errorMsg);
    }

    public void checkPasswordError(String errorMsg) {
        robot.assertPasswordError(errorMsg);
    }

    public void checkRepeatPasswordError(String errorMsg) {
        robot.assertRepeatPasswordError(errorMsg);
    }

    public void checkInLogin() {
        robot.assertInLogin();
    }

    public void sendWithKeyboard() {
        robot.sendWithKeyboard();
    }

    public void checkRegisterDisabled() {
        robot.assertRegisterDisabled();
    }
}