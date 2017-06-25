package ru.ravens.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateWorker {

    public static Date dateFromString(String date) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS");
        String[] sp = date.split(":");
        if (sp.length == 4) {
            int l = sp[3].length();
            String nilstr = "";

            for (int i = l; i < 3; i++) {
                nilstr += "0";
            }

            sp[3] = sp[3] + nilstr;
            date = sp[0] + ":" + sp[1] + ":" + sp[2] + ":" + sp[3];
            return dateFormat.parse(date);
        } else {
            dateFormat = new SimpleDateFormat("yyyy.MM.dd");
            return dateFormat.parse(date);
        }
    }

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
