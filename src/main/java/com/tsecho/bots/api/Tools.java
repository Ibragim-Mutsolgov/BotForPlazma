package com.tsecho.bots.api;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Tools {

    public static String getSalutations() {
        return getSalutations(new Date());
    }

    public static String getSalutations(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if ((hour >= 22 && hour <= 24) || (hour >= 0 && hour <= 4)) {
            return "Доброй ночи, ";
        } else if (hour >= 5 && hour <= 10) {
            return "Доброе утро, ";
        } else if (hour >= 11 && hour <= 17) {
            return "Добрый день, ";
        } else {
            return "Доброе вечер, ";
        }
    }
}
