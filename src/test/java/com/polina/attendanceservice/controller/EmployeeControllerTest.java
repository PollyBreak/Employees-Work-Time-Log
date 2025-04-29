package com.polina.attendanceservice.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.polina.attendanceservice.controller.rest.EmployeeController;
import com.polina.attendanceservice.dto.AttendanceRecordResponse;
import com.polina.attendanceservice.dto.EmployeeRequest;
import com.polina.attendanceservice.entity.Employee;
import com.polina.attendanceservice.service.AttendanceService;
import com.polina.attendanceservice.service.EmployeeService;
import com.polina.attendanceservice.util.RangeType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @MockitoBean
    private AttendanceService attendanceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_shouldReturnCreatedEmployee() throws Exception {
        EmployeeRequest request = new EmployeeRequest();
        request.setName("Alice");
        request.setSurname("Smith");
        request.setEmail("alice@example.com");
        request.setPhone("123456789");
        request.setRoom("B3");
        request.setPosition("Engineer");
        request.setCompanyId(1L);

        Employee employee = new Employee();
        employee.setId(10L);
        employee.setName("Alice");
        employee.setSurname("Smith");
        employee.setEmail("alice@example.com");
        employee.setPhone("123456789");
        employee.setRoom("B3");
        employee.setPosition("Engineer");

        when(employeeService.registerEmployee(any(EmployeeRequest.class))).thenReturn(employee);

        mockMvc.perform(post("/api/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employee.getId()))
                .andExpect(jsonPath("$.name").value(employee.getName()))
                .andExpect(jsonPath("$.surname").value(employee.getSurname()));

        verify(employeeService).registerEmployee(any(EmployeeRequest.class));
    }

    @Test
    void getAll_shouldReturnEmployeeList() throws Exception {
        Employee employee = new Employee();
        employee.setId(20L);
        employee.setName("John");
        employee.setSurname("Doe");

        when(employeeService.getAllEmployees()).thenReturn(List.of(employee));

        mockMvc.perform(get("/api/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(employee.getId()))
                .andExpect(jsonPath("$[0].name").value(employee.getName()))
                .andExpect(jsonPath("$[0].surname").value(employee.getSurname()));

        verify(employeeService).getAllEmployees();
    }

    @Test
    void getEmployeeHistory_shouldReturnAttendance_defaultRange() throws Exception {
        AttendanceRecordResponse response = new AttendanceRecordResponse(
                10L, "Alice", true, LocalDateTime.now());

        when(attendanceService.getEmployeeAttendance(10L, RangeType.ALL))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/employee/10/attendance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeId").value(response.getEmployeeId()))
                .andExpect(jsonPath("$[0].employeeName").value(response.getEmployeeName()));

        verify(attendanceService).getEmployeeAttendance(10L, RangeType.ALL);
    }

    @Test
    void getEmployeeHistory_shouldSupportRangeQueryParam_caseInsensitive() throws Exception {
        when(attendanceService.getEmployeeAttendance(33L, RangeType.MONTH)).thenReturn(List.of());

        mockMvc.perform(get("/api/employee/33/attendance?range=month"))
                .andExpect(status().isOk());

        verify(attendanceService).getEmployeeAttendance(33L, RangeType.MONTH);
    }
}
