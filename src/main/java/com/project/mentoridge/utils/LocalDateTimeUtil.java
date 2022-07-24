package com.project.mentoridge.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeUtil {
// TODO - 예외 처리
    public static String getNowToString(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String getDateToString(LocalDate date) {
        if (date == null) {
            // return StringUtils.EMPTY;
            return null;
        }
        return date.format(DateTimeFormatter.ISO_DATE);
    }

    public static String getDateTimeToString(LocalDateTime dateTime) {
        if (dateTime == null) {
            // return StringUtils.EMPTY;
            return null;
        }
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    // parse
    public static LocalDate getStringToDate(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        return LocalDate.parse(date);
    }

}
