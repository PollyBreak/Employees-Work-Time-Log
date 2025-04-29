package com.polina.attendanceservice.service;

import com.polina.attendanceservice.entity.BusinessTrip;
import com.polina.attendanceservice.entity.Company;
import com.polina.attendanceservice.entity.Employee;
import com.polina.attendanceservice.repository.BusinessTripRepository;
import com.polina.attendanceservice.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BusinessTripServiceTest {

    private EmployeeRepository employeeRepo;
    private BusinessTripRepository tripRepo;
    private BusinessTripService service;

    @BeforeEach
    void setUp() {
        employeeRepo = mock(EmployeeRepository.class);
        tripRepo = mock(BusinessTripRepository.class);
        service = new BusinessTripService(employeeRepo, tripRepo);
    }

    @Test
    void registerTrip_shouldRegisterSuccessfully() {
        Long companyId = 1L;
        String email = "user@example.com";
        LocalDate today = LocalDate.now();

        Company company = new Company();
        company.setId(companyId);

        Employee employee = new Employee();
        employee.setCompany(company);

        when(employeeRepo.findByEmail(email)).thenReturn(Optional.of(employee));
        when(tripRepo.existsByEmployeeAndDate(employee, today)).thenReturn(false);

        service.registerTrip(companyId, email);

        verify(tripRepo).save(any(BusinessTrip.class));
    }

    @Test
    void registerTrip_shouldThrowIfEmployeeNotInCompany() {
        Long companyId = 1L;
        String email = "user@example.com";

        Company company = new Company();
        company.setId(2L);

        Employee employee = new Employee();
        employee.setCompany(company);

        when(employeeRepo.findByEmail(email)).thenReturn(Optional.of(employee));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.registerTrip(companyId, email));

        assertEquals("Email не найден в компании", ex.getMessage());
        verify(tripRepo, never()).save(any());
    }

    @Test
    void registerTrip_shouldThrowIfAlreadyMarkedToday() {
        Long companyId = 1L;
        String email = "user@example.com";
        LocalDate today = LocalDate.now();

        Company company = new Company();
        company.setId(companyId);

        Employee employee = new Employee();
        employee.setCompany(company);

        when(employeeRepo.findByEmail(email)).thenReturn(Optional.of(employee));
        when(tripRepo.existsByEmployeeAndDate(employee, today)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.registerTrip(companyId, email));

        assertEquals("Вы уже отметились сегодня", ex.getMessage());
        verify(tripRepo, never()).save(any());
    }

    @Test
    void registerTrip_shouldThrowIfEmailNotFound() {
        when(employeeRepo.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.registerTrip(1L, "unknown@example.com"));

        assertEquals("Email не найден в компании", ex.getMessage());
        verify(tripRepo, never()).save(any());
    }
}
