package com.polina.attendanceservice.service;


import com.polina.attendanceservice.dto.AttendanceRecordResponse;
import com.polina.attendanceservice.dto.EmployeeStatusView;
import com.polina.attendanceservice.entity.Attendance;
import com.polina.attendanceservice.entity.Company;
import com.polina.attendanceservice.entity.Employee;
import com.polina.attendanceservice.exception.NotFoundException;
import com.polina.attendanceservice.repository.AttendanceRepository;
import com.polina.attendanceservice.repository.CompanyRepository;
import com.polina.attendanceservice.repository.EmployeeRepository;
import com.polina.attendanceservice.util.RangeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.polina.attendanceservice.util.RangeType.DAY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AttendanceServiceTest {

    private EmployeeRepository employeeRepo;
    private AttendanceRepository attendanceRepo;
    private CompanyRepository companyRepo;
    private AttendanceService service;

    private final LocalDateTime fixedNow = LocalDateTime.of(2024, 4, 1, 9, 0);

    @BeforeEach
    void setUp() {
        employeeRepo = mock(EmployeeRepository.class);
        attendanceRepo = mock(AttendanceRepository.class);
        companyRepo = mock(CompanyRepository.class);
        service = new AttendanceService(employeeRepo, attendanceRepo, companyRepo);
    }

    @Test
    void processScan_shouldReturnNull_whenEmployeeNotFoundOrWrongCompany() {
        when(employeeRepo.findByEmail("john@example.com")).thenReturn(Optional.empty());
        String result = service.processScan(1L, "john@example.com");
        assertNull(result);
    }

    @Test
    void processScan_shouldToggleStatusAndSaveAttendance_whenEmployeeFound() {
        Company company = new Company();
        company.setId(1L);

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setCompany(company);

        Attendance previous = new Attendance();
        previous.setAtWork(true);

        when(employeeRepo.findByEmail("john@example.com")).thenReturn(Optional.of(employee));
        when(attendanceRepo.findTopByEmployeeOrderByTimestampDesc(employee)).thenReturn(Optional.of(previous));

        String result = service.processScan(1L, "john@example.com");

        assertEquals("Вы ушли с работы!", result);
        verify(attendanceRepo).save(any(Attendance.class));
    }

    @Test
    void processScan_shouldMarkAsAtWork_whenNoPreviousAttendance() {
        Company company = new Company();
        company.setId(1L);

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setCompany(company);

        when(employeeRepo.findByEmail("john@example.com")).thenReturn(Optional.of(employee));
        when(attendanceRepo.findTopByEmployeeOrderByTimestampDesc(employee)).thenReturn(Optional.empty());

        String result = service.processScan(1L, "john@example.com");

        assertEquals("Вы успешно отметились!", result);
        verify(attendanceRepo).save(any(Attendance.class));
    }

    @Test
    void getEmployeeAttendance_shouldThrowIfEmployeeNotFound() {
        when(employeeRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getEmployeeAttendance(1L, DAY));
    }

    @Test
    void getEmployeeAttendance_shouldReturnMappedResults() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("John");
        employee.setSurname("Doe");

        Attendance attendance = new Attendance();
        attendance.setAtWork(true);
        attendance.setTimestamp(fixedNow);
        attendance.setEmployee(employee);

        when(employeeRepo.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepo.findByEmployeeAndTimestampAfterOrderByTimestampDesc(eq(employee), any()))
                .thenReturn(List.of(attendance));

        List<AttendanceRecordResponse> result = service.getEmployeeAttendance(1L, DAY);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getEmployeeName());
    }

    @Test
    void getCompanyAttendance_shouldThrowIfCompanyNotFound() {
        when(companyRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getCompanyAttendance(1L, DAY));
    }

    @Test
    void getCompanyAttendance_shouldReturnAttendanceForAllEmployees() {
        Company company = new Company();
        company.setId(1L);

        Employee e = new Employee();
        e.setId(1L);
        e.setName("Alice");
        e.setSurname("Smith");
        e.setCompany(company);

        Attendance a = new Attendance();
        a.setAtWork(true);
        a.setTimestamp(fixedNow);
        a.setEmployee(e);

        when(companyRepo.findById(1L)).thenReturn(Optional.of(company));
        when(employeeRepo.findAll()).thenReturn(List.of(e));
        when(attendanceRepo.findByEmployeeAndTimestampAfterOrderByTimestampDesc(eq(e), any()))
                .thenReturn(List.of(a));

        List<AttendanceRecordResponse> result = service.getCompanyAttendance(1L, DAY);
        assertEquals(1, result.size());
        assertEquals("Alice Smith", result.get(0).getEmployeeName());
    }

    @Test
    void getCompanyEmployeesWithStatus_shouldThrowIfCompanyNotFound() {
        when(companyRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getCompanyEmployeesWithStatus(1L));
    }

    @Test
    void getCompanyEmployeesWithStatus_shouldReturnAllEmployeesWithStatus() {
        Company company = new Company();
        company.setId(1L);

        Employee e = new Employee();
        e.setId(1L);
        e.setName("Bob");
        e.setSurname("Brown");
        e.setCompany(company);
        e.setPhone("123");
        e.setPosition("Dev");
        e.setRoom("101");

        Attendance a = new Attendance();
        a.setAtWork(true);

        when(companyRepo.findById(1L)).thenReturn(Optional.of(company));
        when(employeeRepo.findAll()).thenReturn(List.of(e));
        when(attendanceRepo.findTopByEmployeeOrderByTimestampDesc(e)).thenReturn(Optional.of(a));

        List<EmployeeStatusView> result = service.getCompanyEmployeesWithStatus(1L);

        assertEquals(1, result.size());
        assertTrue(result.get(0).isAtWork());
        assertEquals("Bob", result.get(0).getName());
    }

    @Test
    void getCompanyEmployeesWithStatus_shouldDefaultToFalseIfNoAttendance() {
        Company company = new Company();
        company.setId(1L);

        Employee e = new Employee();
        e.setId(1L);
        e.setName("NoAttend");
        e.setSurname("Emp");
        e.setCompany(company);

        when(companyRepo.findById(1L)).thenReturn(Optional.of(company));
        when(employeeRepo.findAll()).thenReturn(List.of(e));
        when(attendanceRepo.findTopByEmployeeOrderByTimestampDesc(e)).thenReturn(Optional.empty());

        List<EmployeeStatusView> result = service.getCompanyEmployeesWithStatus(1L);
        assertFalse(result.get(0).isAtWork());
    }
}
