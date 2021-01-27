package ru.job4j.grabber;

import org.junit.Test;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class DateParserTest {
    @Test
    public void checkDayMonthYear() {
        Calendar parsed = DateParser.parseDateString("2 дек 19, 22:29");
        assertNotNull(parsed);
        String parsedDate   = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(parsed.getTime());
        String expectedDate = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(new Date(2019 - 1900, Calendar.DECEMBER, 2, 22, 29));
        assertEquals(expectedDate, parsedDate);

        parsed = DateParser.parseDateString("25 июн 18, 21:56");
        assertNotNull(parsed);
        parsedDate   = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(parsed.getTime());
        expectedDate = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(new Date(2018 - 1900, Calendar.JUNE, 25, 21, 56));
        assertEquals(expectedDate, parsedDate);

        parsed = DateParser.parseDateString("22 янв 16, 10:56");
        assertNotNull(parsed);
        parsedDate   = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(parsed.getTime());
        expectedDate = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(new Date(2016 - 1900, Calendar.JANUARY, 22, 10, 56));
        assertEquals(expectedDate, parsedDate);

        parsed = DateParser.parseDateString("12 май 20, 08:17");
        assertNotNull(parsed);
        parsedDate   = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(parsed.getTime());
        expectedDate = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(new Date(2020 - 1900, Calendar.MAY, 12, 8, 17));
        assertEquals(expectedDate, parsedDate);
    }

    @Test
    public void checkTodayYesterday() {
        Calendar parsed = DateParser.parseDateString("сегодня, 02:30");
        assertNotNull(parsed);
        String parsedDate   = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(parsed.getTime());
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 2);
        today.set(Calendar.MINUTE, 30);
        String expectedDate = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(today.getTime());
        assertEquals(expectedDate, parsedDate);

        parsed = DateParser.parseDateString("вчера, 19:23");
        assertNotNull(parsed);
        parsedDate   = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(parsed.getTime());
        Calendar yesterday = Calendar.getInstance();
        yesterday.set(Calendar.HOUR_OF_DAY, 19);
        yesterday.set(Calendar.MINUTE, 23);
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        expectedDate = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(yesterday.getTime());
        assertEquals(expectedDate, parsedDate);
    }
}