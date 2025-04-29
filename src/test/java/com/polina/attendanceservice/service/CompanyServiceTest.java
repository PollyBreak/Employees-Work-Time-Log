package com.polina.attendanceservice.service;

import com.polina.attendanceservice.dto.CompanyRequest;
import com.polina.attendanceservice.dto.CompanyStatusResponse;
import com.polina.attendanceservice.dto.EmployeeStatusResponse;
import com.polina.attendanceservice.entity.Attendance;
import com.polina.attendanceservice.entity.Company;
import com.polina.attendanceservice.entity.Employee;
import com.polina.attendanceservice.exception.NotFoundException;
import com.polina.attendanceservice.repository.AttendanceRepository;
import com.polina.attendanceservice.repository.CompanyRepository;
import com.polina.attendanceservice.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompanyServiceTest {

    private CompanyRepository companyRepo;
    private EmployeeRepository employeeRepo;
    private AttendanceRepository attendanceRepo;
    private CompanyService service;

    @BeforeEach
    void setUp() {
        companyRepo = mock(CompanyRepository.class);
        employeeRepo = mock(EmployeeRepository.class);
        attendanceRepo = mock(AttendanceRepository.class);
        service = new CompanyService(companyRepo, employeeRepo, attendanceRepo);
    }

    @Test
    void createCompany_shouldSaveAndReturnCompany() {
        CompanyRequest request = new CompanyRequest();
        Company saved = new Company();
        saved.setId(1L);
        saved.setName("TestCo");

        when(companyRepo.save(any(Company.class))).thenReturn(saved);

        Company result = service.createCompany(request);

        assertEquals("TestCo", result.getName());
        assertEquals(1L, result.getId());
        verify(companyRepo).save(any(Company.class));
    }

    @Test
    void getAllCompanies_shouldReturnList() {
        Company c1 = new Company();
        c1.setId(1L);
        Company c2 = new Company();
        c2.setId(2L);

        when(companyRepo.findAll()).thenReturn(List.of(c1, c2));

        List<Company> companies = service.getAllCompanies();

        assertEquals(2, companies.size());
        assertEquals(1L, companies.get(0).getId());
        assertEquals(2L, companies.get(1).getId());
    }

    @Test
    void getCompanyWithStatuses_shouldReturnCompanyWithEmployeeStatuses() {
        Long companyId = 1L;

        Company company = new Company();
        company.setId(companyId);
        company.setName("TechCorp");

        Employee emp1 = new Employee();
        emp1.setId(10L);
        emp1.setName("Alice");
        emp1.setSurname("Smith");
        emp1.setCompany(company);

        Employee emp2 = new Employee();
        emp2.setId(11L);
        emp2.setName("Bob");
        emp2.setSurname("Johnson");
        emp2.setCompany(company);

        Attendance att = new Attendance();
        att.setAtWork(true);

        when(companyRepo.findById(companyId)).thenReturn(Optional.of(company));
        when(employeeRepo.findAll()).thenReturn(List.of(emp1, emp2));
        when(attendanceRepo.findTopByEmployeeOrderByTimestampDesc(emp1)).thenReturn(Optional.of(att));
        when(attendanceRepo.findTopByEmployeeOrderByTimestampDesc(emp2)).thenReturn(Optional.empty());

        CompanyStatusResponse response = service.getCompanyWithStatuses(companyId);

        assertEquals(companyId, response.getId());
        assertEquals("TechCorp", response.getName());
        assertEquals(2, response.getEmployees().size());

        EmployeeStatusResponse e1 = response.getEmployees().get(0);
        EmployeeStatusResponse e2 = response.getEmployees().get(1);

        assertEquals(emp1.getId(), e1.getId());
        assertTrue(e1.isAtWork());

        assertEquals(emp2.getId(), e2.getId());
        assertFalse(e2.isAtWork());
    }

    @Test
    void getCompanyWithStatuses_shouldThrowIfCompanyNotFound() {
        when(companyRepo.findById(999L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                service.getCompanyWithStatuses(999L));

        assertTrue(ex.getMessage().contains("Company"));
        assertTrue(ex.getMessage().contains("999"));
    }
}
