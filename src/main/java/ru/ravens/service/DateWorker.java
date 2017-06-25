package ru.ravens.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateWorker {


    public static String makeSqlDateString(Date date) {
        DateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

        if (format.format(date).equals("00:00:00:000")) {
            return dateFormat.format(date);
        } else {
            return dateFormat.format(date) + " " + format.format(date);
        }
    }

    public static String getNowMomentInUTC() {
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        return calendar.get(Calendar.YEAR) + "."
                + (calendar.get(Calendar.MONTH) + 1) + "."
                + calendar.get(Calendar.DAY_OF_MONTH) + " "
                + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                + calendar.get(Calendar.MINUTE) + ":"
                + calendar.get(Calendar.SECOND) + ":"
                + calendar.get(Calendar.MILLISECOND);
    }
}
