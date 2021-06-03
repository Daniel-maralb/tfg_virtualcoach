package com.example.virtualcoach.recommendations;

import com.example.virtualcoach.usermanagement.model.DisplayUser;

public class UserData {

    public final double hrTooHigh;
    public final double hrHigh;
    public final double hrLow;

    public final double imc;

    private final DisplayUser user;

    public UserData(DisplayUser user) {
        this.user = user;

        double maxHR = 220 - user.age;

        this.hrTooHigh = 0.85 * maxHR;
        this.hrHigh = 0.7 * maxHR;
        this.hrLow = 0.5 * maxHR;

        double heightInM = (double) user.height / 100;
        imc = user.weight / heightInM * heightInM;
    }

    public boolean isOld() {
        return user.age > 60;
    }

    public boolean isIMCVeryLow() {
        return imc < 18.5;
    }

    public boolean isIMCNormal() {
        return 18.5 <= imc && imc < 25;
    }

    public boolean isIMCHigh() {
        return 25 <= imc && imc < 30;
    }

    public boolean isIMCVeryHigh() {
        return imc > 30;
    }

}
