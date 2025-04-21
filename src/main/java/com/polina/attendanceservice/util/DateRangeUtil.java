package com.polina.attendanceservice.util;


import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateRangeUtil {
    public static LocalDateTime getStartTime(RangeType rangeType) {
        return switch (rangeType) {
            case DAY -> LocalDate.now().atStartOfDay();
            case WEEK -> LocalDate.now().minusDays(7).atStartOfDay();
            case MONTH -> LocalDate.now().withDayOfMonth(1).atStartOfDay();
            case ALL -> LocalDate.of(2000, 1, 1).atStartOfDay();
        };
    }
}
