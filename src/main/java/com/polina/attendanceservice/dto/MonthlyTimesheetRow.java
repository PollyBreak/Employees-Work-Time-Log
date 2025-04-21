package com.polina.attendanceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
public class MonthlyTimesheetRow {
    private Long employeeId;
    private String name;
    private String surname;
    private String position;
    private String room;

    private Map<LocalDate, Duration> workedHoursPerDay;
    private Duration totalWorked;
}
