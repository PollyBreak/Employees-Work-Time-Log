package com.polina.attendanceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class DailyWorkRecord {
    private LocalDate date;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private Duration duration;
}
