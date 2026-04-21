package com.build.ecommerce.common.support.time;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateUtil {

    public final static String LOCAL_DATE_PATTERN = "yyyy-MM-dd";

    public static String toString(LocalDate localDateObject) {
        return localDateObject.format(DateTimeFormatter.ofPattern(LOCAL_DATE_PATTERN));
    }

    public static LocalDate toLocalDate(String localDateString) {
        return LocalDate.parse(localDateString, DateTimeFormatter.ofPattern(LOCAL_DATE_PATTERN));
    }
}
