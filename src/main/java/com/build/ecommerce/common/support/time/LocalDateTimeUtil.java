package com.build.ecommerce.common.support.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeUtil {

    public final static String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static String toString(LocalDate localDateObject) {
        return localDateObject.format(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN));
    }

    public static LocalDateTime toLocalDate(String localDateString) {
        return LocalDateTime.parse(localDateString, DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN));
    }

    public static String nowToString() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN));
    }

}
