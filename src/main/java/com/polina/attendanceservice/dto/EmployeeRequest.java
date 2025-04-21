package com.polina.attendanceservice.dto;

import lombok.Data;

@Data
public class EmployeeRequest {
    private String name;
    private String surname;
    private String macAddress;
    private Long companyId;

    private String phone;
    private String room;
    private String position;
}
