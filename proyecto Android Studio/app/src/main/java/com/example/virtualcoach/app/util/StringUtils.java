package com.example.virtualcoach.app.util;

import static com.example.virtualcoach.app.util.NullUtils.isNull;

public class StringUtils {
    public static boolean isEmpty(String data) {
        return isNull(data) || data.isEmpty();
    }

    public static String capitalize(String data) {
        if (isEmpty(data)) return data;

        return data.substring(0, 1).toUpperCase() + data.substring(1);
    }
}
