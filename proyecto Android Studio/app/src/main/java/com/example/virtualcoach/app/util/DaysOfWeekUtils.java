package com.example.virtualcoach.app.util;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.example.virtualcoach.app.util.NullUtils.isNull;

public class DaysOfWeekUtils {
    private static final String CHAR_TRANSLATION = "LMXJVSD";

    public static List<DayOfWeek> from(String data) {
        if (isNull(data)) return Collections.emptyList();

        List<DayOfWeek> result = new ArrayList<>(data.length());

        for (char c : data.toCharArray()) {
            result.add(DayOfWeek.of(CHAR_TRANSLATION.indexOf(c) + 1));
        }
        return result;
    }

    public static List<DayOfWeek> from(boolean[] data) {
        List<DayOfWeek> result = new ArrayList<>(data.length);

        for (int i = 0; i < data.length; i++) {
            if (data[i])
                result.add(DayOfWeek.of(i + 1));
        }

        return result;
    }

    public static boolean[] getBooleanArray(List<DayOfWeek> days) {
        boolean[] result = new boolean[7];
        Arrays.fill(result, false);

        if (isNull(days)) return result;

        for (DayOfWeek d : days) {
            result[d.getValue() - 1] = true;
        }
        return result;
    }

    public static String getString(List<DayOfWeek> days) {
        StringBuilder result = new StringBuilder();

        for (DayOfWeek d : days) {
            result.append(CHAR_TRANSLATION.charAt(d.getValue() - 1));
        }

        return result.toString();
    }

    public static boolean equals(List<DayOfWeek> list1, List<DayOfWeek> list2) {
        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }
}
