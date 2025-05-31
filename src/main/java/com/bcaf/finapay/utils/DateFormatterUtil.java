package com.bcaf.finapay.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class DateFormatterUtil {
    private static final Locale LOCALE_ID = new Locale("id", "ID");

    public static LocalDate convertLongIndonesianDateToLocalDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", LOCALE_ID);
        return LocalDate.parse(dateStr, formatter);
    }

    public static String formatIsoToIndonesianDate(String isoDateStr) {
        LocalDate date = LocalDate.parse(isoDateStr);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", LOCALE_ID);
        return date.format(formatter);
    }

    public static String formatToIndonesianDate(LocalDateTime dateTime) {
        if (dateTime == null)
            return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", LOCALE_ID);
        return dateTime.toLocalDate().format(formatter);
    }

    public static String addOneMonthToLongIndonesianDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", LOCALE_ID);
        LocalDate date = LocalDate.parse(dateStr, formatter);
        LocalDate plusOneMonth = date.plusMonths(1);
        return plusOneMonth.format(formatter);
    }

}
