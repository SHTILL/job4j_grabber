package ru.job4j.grabber;

import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DateParser {
    final static HashMap<String, Integer> MONTHS;
    static {
        MONTHS = new HashMap<>();
        MONTHS.put("янв", Calendar.JANUARY);
        MONTHS.put("фев", Calendar.FEBRUARY);
        MONTHS.put("мар", Calendar.MARCH);
        MONTHS.put("апр", Calendar.APRIL);
        MONTHS.put("май", Calendar.MAY);
        MONTHS.put("июн", Calendar.JUNE);
        MONTHS.put("июл", Calendar.JULY);
        MONTHS.put("авг", Calendar.AUGUST);
        MONTHS.put("сен", Calendar.SEPTEMBER);
        MONTHS.put("окт", Calendar.OCTOBER);
        MONTHS.put("ноя", Calendar.NOVEMBER);
        MONTHS.put("дек", Calendar.DECEMBER);
    }

    static Calendar parseDateString(String sqlRuDate) {
        Pattern p1 = Pattern.compile("^\\d{1,2}+ [\\p{Ll}]++ \\d{2,3}+, \\d{2}+:\\d{2}+$");
        Matcher m1 = p1.matcher(sqlRuDate);
        boolean p1IsMatched = m1.matches();

        Pattern p2 = Pattern.compile("^[\\p{Ll}]{5,7}+, \\d{2}+:\\d{2}+$");
        Matcher m2 = p2.matcher(sqlRuDate);
        boolean p2IsMatched = m2.matches();

        if (!p1IsMatched && !p2IsMatched) {
            return null;
        }

        String[] sqlRuTokens = sqlRuDate.split(", ", 2);
        if (sqlRuTokens.length != 2) {
            return null;
        }

        Calendar c = Calendar.getInstance();

        String dateString = sqlRuTokens[0];
        String timeString = sqlRuTokens[1];

        if (p1IsMatched) {
            String[] dateTokens = dateString.split(" ", 3);
            if (dateTokens.length != 3) {
                return null;
            }
            int day   = Integer.parseInt(dateTokens[0]);
            int year  = 2000 + Integer.parseInt(dateTokens[2]);
            if (!MONTHS.containsKey(dateTokens[1])) {
                return null;
            }
            int month = MONTHS.get(dateTokens[1]);
            c.set(year, month, day);
        }
        if (p2IsMatched) {
            switch (dateString) {
                case "сегодня":
                    break;
                case "вчера":
                    c.add(Calendar.DAY_OF_MONTH, -1);
                    break;
                default:
                    return null;
            }
        }

        String[] timeTokens = timeString.split(":", 2);
        if (timeTokens.length != 2) {
            return null;
        }
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeTokens[0]));
        c.set(Calendar.MINUTE, Integer.parseInt(timeTokens[1]));
        return c;
    }
}
