package com.polina.attendanceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AttendanceRecordResponse {
    private Long employeeId;
    private String employeeName;
    private boolean atWork;
    private LocalDateTime timestamp;
}
