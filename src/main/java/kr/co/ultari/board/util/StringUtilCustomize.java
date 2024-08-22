package kr.co.ultari.board.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class StringUtilCustomize {
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static String castNowDate(LocalDateTime dateTime) {
        DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(datePattern);
    }

    public static String getNowDate(String pattern) {
        DateTimeFormatter datePattern = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.now().format(datePattern).toString();
    }
}
