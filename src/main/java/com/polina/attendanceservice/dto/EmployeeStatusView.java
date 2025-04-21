package com.polina.attendanceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeStatusView {
    private Long id;
    private String name;
    private String surname;
    private String room;
    private String phone;
    private String position;
    private boolean atWork;
}
