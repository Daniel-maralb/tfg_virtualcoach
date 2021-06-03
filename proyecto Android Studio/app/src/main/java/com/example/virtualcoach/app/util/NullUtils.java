package com.example.virtualcoach.app.util;

// Provides functions to avoid null checks
public class NullUtils {
    // Objects.isNull substitute
    public static boolean isNull(Object o) {
        return o == null;
    }

    @SafeVarargs
    public static <T> T firstNonNull(T... objects) {
        for (T obj : objects) {
            if (!isNull(obj))
                return obj;
        }
        return null;
    }

    public static boolean anyNull(Object... objects) {
        for (Object o : objects) {
            if (isNull(o))
                return true;
        }
        return false;
    }
}
