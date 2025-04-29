package com.polina.attendanceservice.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.polina.attendanceservice.controller.rest.CompanyController;
import com.polina.attendanceservice.dto.AttendanceRecordResponse;
import com.polina.attendanceservice.dto.CompanyRequest;
import com.polina.attendanceservice.dto.CompanyStatusResponse;
import com.polina.attendanceservice.entity.Company;
import com.polina.attendanceservice.service.AttendanceService;
import com.polina.attendanceservice.service.CompanyService;
import com.polina.attendanceservice.util.RangeType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompanyController.class)
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompanyService companyService;

    @MockitoBean
    private AttendanceService attendanceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_shouldReturnSavedCompany() throws Exception {
        CompanyRequest request = new CompanyRequest();
        Company company = new Company();
        company.setId(1L);
        company.setName("NewCo");

        when(companyService.createCompany(any(CompanyRequest.class))).thenReturn(company);

        mockMvc.perform(post("/api/company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(company.getId()))
                .andExpect(jsonPath("$.name").value(company.getName()));

        verify(companyService).createCompany(any(CompanyRequest.class));
    }

    @Test
    void getAll_shouldReturnCompanyList() throws Exception {
        Company company = new Company();
        company.setId(1L);
        company.setName("Company A");

        when(companyService.getAllCompanies()).thenReturn(List.of(company));

        mockMvc.perform(get("/api/company"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(company.getId()))
                .andExpect(jsonPath("$[0].name").value(company.getName()));

        verify(companyService).getAllCompanies();
    }

    @Test
    void getWithStatus_shouldReturnCompanyStatusResponse() throws Exception {
        CompanyStatusResponse response = new CompanyStatusResponse(1L, "Company A", List.of());

        when(companyService.getCompanyWithStatuses(1L)).thenReturn(response);

        mockMvc.perform(get("/api/company/1/employees/statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.employees").isArray());

        verify(companyService).getCompanyWithStatuses(1L);
    }

    @Test
    void getCompanyHistory_shouldReturnAttendanceRecords_defaultRange() throws Exception {
        AttendanceRecordResponse record = new AttendanceRecordResponse(
                1L, "John", true, LocalDateTime.now());

        when(attendanceService.getCompanyAttendance(1L, RangeType.ALL))
                .thenReturn(List.of(record));

        mockMvc.perform(get("/api/company/1/attendance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeId").value(record.getEmployeeId()))
                .andExpect(jsonPath("$[0].employeeName").value(record.getEmployeeName()));

        verify(attendanceService).getCompanyAttendance(1L, RangeType.ALL);
    }

    @Test
    void getCompanyHistory_shouldSupportRangeParam_caseInsensitive() throws Exception {
        when(attendanceService.getCompanyAttendance(2L, RangeType.MONTH)).thenReturn(List.of());

        mockMvc.perform(get("/api/company/2/attendance?range=month"))
                .andExpect(status().isOk());

        verify(attendanceService).getCompanyAttendance(2L, RangeType.MONTH);
    }
}
