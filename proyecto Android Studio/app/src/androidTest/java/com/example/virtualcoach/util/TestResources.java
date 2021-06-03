package com.example.virtualcoach.util;

import androidx.annotation.StringRes;
import androidx.test.core.app.ApplicationProvider;

public class TestResources {
    public static String getString(@StringRes Integer resId) {
        return ApplicationProvider.getApplicationContext().getString(resId);
    }

    public static void waitForTransition() {
        sleep(100);
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
