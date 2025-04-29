package com.polina.attendanceservice.service;

import com.polina.attendanceservice.dto.EmployeeRequest;
import com.polina.attendanceservice.entity.Company;
import com.polina.attendanceservice.entity.Employee;
import com.polina.attendanceservice.exception.NotFoundException;
import com.polina.attendanceservice.repository.CompanyRepository;
import com.polina.attendanceservice.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    private EmployeeRepository employeeRepo;
    private CompanyRepository companyRepo;
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeRepo = mock(EmployeeRepository.class);
        companyRepo = mock(CompanyRepository.class);
        employeeService = new EmployeeService(employeeRepo, companyRepo);
    }

    @Test
    void registerEmployee_shouldCreateAndReturnEmployee() {
        EmployeeRequest request = new EmployeeRequest();
        request.setName("John");
        request.setSurname("Doe");
        request.setEmail("john.doe@example.com");
        request.setPhone("123-456-7890");
        request.setRoom("101");
        request.setPosition("Engineer");
        request.setCompanyId(1L);

        Company company = new Company();
        company.setId(1L);
        company.setName("TechCo");

        Employee savedEmployee = new Employee();
        savedEmployee.setId(100L);
        savedEmployee.setName("John");

        when(companyRepo.findById(1L)).thenReturn(Optional.of(company));
        when(employeeRepo.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
        when(employeeRepo.save(any(Employee.class))).thenReturn(savedEmployee);

        Employee result = employeeService.registerEmployee(request);

        assertEquals(100L, result.getId());
        assertEquals("John", result.getName());

        verify(companyRepo).findById(1L);
        verify(employeeRepo).findByEmail("john.doe@example.com");
        verify(employeeRepo).save(any(Employee.class));
    }

    @Test
    void registerEmployee_shouldThrowWhenCompanyNotFound() {
        EmployeeRequest request = new EmployeeRequest();
        request.setCompanyId(999L);

        when(companyRepo.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> employeeService.registerEmployee(request));

        assertTrue(exception.getMessage().contains("Company"));
        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    void registerEmployee_shouldThrowWhenEmailAlreadyUsed() {
        EmployeeRequest request = new EmployeeRequest();
        request.setEmail("used@example.com");
        request.setCompanyId(1L);

        Company company = new Company();
        company.setId(1L);

        Employee existingEmployee = new Employee();
        existingEmployee.setId(123L);

        when(companyRepo.findById(1L)).thenReturn(Optional.of(company));
        when(employeeRepo.findByEmail("used@example.com")).thenReturn(Optional.of(existingEmployee));

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> employeeService.registerEmployee(request));

        assertTrue(ex.getMessage().contains("used@example.com"));
        assertTrue(ex.getMessage().contains("123"));
    }

    @Test
    void getAllEmployees_shouldReturnList() {
        Employee e1 = new Employee();
        e1.setId(1L);
        Employee e2 = new Employee();
        e2.setId(2L);

        when(employeeRepo.findAll()).thenReturn(List.of(e1, e2));

        List<Employee> result = employeeService.getAllEmployees();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }
}
