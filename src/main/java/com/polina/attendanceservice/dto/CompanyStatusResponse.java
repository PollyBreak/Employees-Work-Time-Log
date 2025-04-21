package com.polina.attendanceservice.dto;

import com.polina.attendanceservice.dto.EmployeeStatusResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CompanyStatusResponse {
    private Long id;
    private String name;
    private List<EmployeeStatusResponse> employees;
}
