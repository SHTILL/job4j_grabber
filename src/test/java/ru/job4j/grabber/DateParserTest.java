package ru.job4j.grabber;

import org.junit.Test;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class DateParserTest {
    private static String dateFormat(Date d) {
        return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(d);
    }

    @Test
    public void checkDayMonthYear() {
        String[][] dates = {{"2 дек 19, 22:29", dateFormat(new Date(2019 - 1900, Calendar.DECEMBER, 2, 22, 29))},
                            {"25 июн 18, 21:56", dateFormat(new Date(2018 - 1900, Calendar.JUNE, 25, 21, 56))},
                            {"22 янв 16, 10:56", dateFormat(new Date(2016 - 1900, Calendar.JANUARY, 22, 10, 56))},
                            {"12 май 20, 08:17", dateFormat(new Date(2020 - 1900, Calendar.MAY, 12, 8, 17))}};

        for (String[] date: dates) {
            String expectedDate = date[1];
            String dateToParse  = date[0];
            Calendar parsed = DateParser.parseDateString(dateToParse);
            assertNotNull(parsed);
            String parsedDate   = dateFormat(parsed.getTime());
            assertEquals(expectedDate, parsedDate);
        }
    }

    @Test
    public void checkTodayYesterday() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 2);
        today.set(Calendar.MINUTE, 30);

        Calendar yesterday = Calendar.getInstance();
        yesterday.set(Calendar.HOUR_OF_DAY, 19);
        yesterday.set(Calendar.MINUTE, 23);
        yesterday.add(Calendar.DAY_OF_MONTH, -1);

        String[][] dates = {{"сегодня, 02:30", dateFormat(today.getTime()) },
                            {"вчера, 19:23",   dateFormat(yesterday.getTime())} };

        for (String[] date: dates) {
            String expectedDate = date[1];
            String dateToParse  = date[0];
            Calendar parsed = DateParser.parseDateString(dateToParse);
            assertNotNull(parsed);
            String parsedDate   = dateFormat(parsed.getTime());
            assertEquals(expectedDate, parsedDate);
        }
    }
}