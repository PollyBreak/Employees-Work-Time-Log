package com.polina.attendanceservice.utls;

import com.polina.attendanceservice.util.DateRangeUtil;
import com.polina.attendanceservice.util.RangeType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateRangeUtilTest {

    @Test
    void getStartTime_whenDayRange_shouldReturnTodayStartOfDay() {
        LocalDateTime expected = LocalDate.now().atStartOfDay();
        LocalDateTime actual = DateRangeUtil.getStartTime(RangeType.DAY);

        assertEquals(expected.toLocalDate(), actual.toLocalDate());
        assertEquals(0, actual.getHour());
        assertEquals(0, actual.getMinute());
    }

    @Test
    void getStartTime_whenWeekRange_shouldReturn7DaysAgoStartOfDay() {
        LocalDateTime expected = LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime actual = DateRangeUtil.getStartTime(RangeType.WEEK);

        assertEquals(expected.toLocalDate(), actual.toLocalDate());
        assertEquals(0, actual.getHour());
        assertEquals(0, actual.getMinute());
    }

    @Test
    void getStartTime_whenMonthRange_shouldReturnFirstDayOfMonthStartOfDay() {
        LocalDateTime expected = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime actual = DateRangeUtil.getStartTime(RangeType.MONTH);

        assertEquals(expected.toLocalDate(), actual.toLocalDate());
        assertEquals(0, actual.getHour());
        assertEquals(0, actual.getMinute());
    }

    @Test
    void getStartTime_whenAllRange_shouldReturnYear2000StartOfDay() {
        LocalDateTime expected = LocalDate.of(2000, 1, 1).atStartOfDay();
        LocalDateTime actual = DateRangeUtil.getStartTime(RangeType.ALL);

        assertEquals(expected, actual);
    }
}
