package com.polina.attendanceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeStatusResponse {
    private Long id;
    private String name;
    private String surname;
    private boolean atWork;
}
