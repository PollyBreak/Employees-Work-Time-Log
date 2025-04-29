package com.polina.attendanceservice.service;

import com.polina.attendanceservice.dto.MonthlyTimesheetRow;
import com.polina.attendanceservice.entity.Attendance;
import com.polina.attendanceservice.entity.BusinessTrip;
import com.polina.attendanceservice.entity.Company;
import com.polina.attendanceservice.entity.Employee;
import com.polina.attendanceservice.exception.NotFoundException;
import com.polina.attendanceservice.repository.AttendanceRepository;
import com.polina.attendanceservice.repository.BusinessTripRepository;
import com.polina.attendanceservice.repository.CompanyRepository;
import com.polina.attendanceservice.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimesheetServiceTest {

    private TimesheetService timesheetService;
    private CompanyRepository companyRepo;
    private EmployeeRepository employeeRepo;
    private AttendanceRepository attendanceRepo;
    private BusinessTripRepository businessTripRepo;

    @BeforeEach
    void setup() {
        companyRepo = mock(CompanyRepository.class);
        employeeRepo = mock(EmployeeRepository.class);
        attendanceRepo = mock(AttendanceRepository.class);
        businessTripRepo = mock(BusinessTripRepository.class);
        timesheetService = new TimesheetService(employeeRepo, companyRepo, attendanceRepo, businessTripRepo);
    }

    @Test
    void generateMonthlyReport_withNormalAttendance_shouldReturnCorrectWorkedTime() {
        Long companyId = 1L;
        YearMonth month = YearMonth.of(2024, 4);
        LocalDate workDate = LocalDate.of(2024, 4, 10);

        Company company = new Company();
        company.setId(companyId);
        when(companyRepo.findById(companyId)).thenReturn(Optional.of(company));

        Employee employee = new Employee();
        employee.setId(10L);
        employee.setName("John");
        employee.setSurname("Doe");
        employee.setPosition("Engineer");
        employee.setRoom("101");
        employee.setCompany(company);
        when(employeeRepo.findAll()).thenReturn(List.of(employee));

        Attendance checkIn = new Attendance();
        checkIn.setAtWork(true);
        checkIn.setTimestamp(workDate.atTime(9, 0));
        checkIn.setEmployee(employee);

        Attendance checkOut = new Attendance();
        checkOut.setAtWork(false);
        checkOut.setTimestamp(workDate.atTime(17, 0));
        checkOut.setEmployee(employee);

        when(businessTripRepo.findByEmployeeAndDateBetween(eq(employee), any(), any()))
                .thenReturn(Collections.emptyList());

        when(attendanceRepo.findByEmployeeAndTimestampBetweenOrderByTimestamp(eq(employee), any(), any()))
                .thenReturn(List.of(checkIn, checkOut));

        List<MonthlyTimesheetRow> result = timesheetService.generateMonthlyReport(companyId, month);

        assertEquals(1, result.size());
        MonthlyTimesheetRow row = result.get(0);
        assertEquals(employee.getId(), row.getEmployeeId());
        assertEquals("John", row.getName());
        assertEquals("Doe", row.getSurname());
        assertEquals("Engineer", row.getPosition());
        assertEquals(Duration.ofHours(8), row.getTotalWorked());
        assertEquals(1, row.getWorkedHoursPerDay().size());
        assertTrue(row.getWorkedHoursPerDay().containsKey(workDate));
        assertEquals(Duration.ofHours(8), row.getWorkedHoursPerDay().get(workDate));
    }

    @Test
    void generateMonthlyReport_withBusinessTrip_shouldRecord8Hours() {
        Long companyId = 2L;
        YearMonth month = YearMonth.of(2024, 4);
        LocalDate tripDate = LocalDate.of(2024, 4, 5);

        Company company = new Company();
        company.setId(companyId);
        when(companyRepo.findById(companyId)).thenReturn(Optional.of(company));

        Employee employee = new Employee();
        employee.setId(20L);
        employee.setName("Alice");
        employee.setSurname("Wong");
        employee.setPosition("Sales");
        employee.setCompany(company);

        when(employeeRepo.findAll()).thenReturn(List.of(employee));

        BusinessTrip trip = new BusinessTrip();
        trip.setDate(tripDate);
        trip.setEmployee(employee);
        when(businessTripRepo.findByEmployeeAndDateBetween(eq(employee), any(), any()))
                .thenReturn(List.of(trip));

        when(attendanceRepo.findByEmployeeAndTimestampBetweenOrderByTimestamp(eq(employee), any(), any()))
                .thenReturn(Collections.emptyList());

        List<MonthlyTimesheetRow> result = timesheetService.generateMonthlyReport(companyId, month);

        assertEquals(1, result.size());
        MonthlyTimesheetRow row = result.get(0);
        assertEquals(Duration.ofHours(8), row.getTotalWorked());
        assertEquals(Duration.ofHours(8), row.getWorkedHoursPerDay().get(tripDate));
        assertEquals("Alice", row.getName());
        assertEquals("Wong", row.getSurname());
        assertEquals("Sales", row.getPosition());
    }

    @Test
    void generateMonthlyReport_withLoneCheckIn_shouldIgnoreUnpairedEntry() {
        Long companyId = 3L;
        YearMonth month = YearMonth.of(2024, 4);
        LocalDate loneDay = LocalDate.of(2024, 4, 6);

        Company company = new Company();
        company.setId(companyId);
        when(companyRepo.findById(companyId)).thenReturn(Optional.of(company));

        Employee employee = new Employee();
        employee.setId(30L);
        employee.setName("Emma");
        employee.setSurname("Stone");
        employee.setPosition("Designer");
        employee.setRoom("302");
        employee.setCompany(company);
        when(employeeRepo.findAll()).thenReturn(List.of(employee));

        Attendance onlyIn = new Attendance();
        onlyIn.setAtWork(true);
        onlyIn.setTimestamp(loneDay.atTime(9, 0));
        onlyIn.setEmployee(employee);

        when(businessTripRepo.findByEmployeeAndDateBetween(eq(employee), any(), any()))
                .thenReturn(Collections.emptyList());

        when(attendanceRepo.findByEmployeeAndTimestampBetweenOrderByTimestamp(eq(employee), any(), any()))
                .thenReturn(List.of(onlyIn));

        List<MonthlyTimesheetRow> result = timesheetService.generateMonthlyReport(companyId, month);

        assertEquals(1, result.size());
        MonthlyTimesheetRow row = result.get(0);
        assertEquals(Duration.ZERO, row.getTotalWorked());
        assertTrue(row.getWorkedHoursPerDay().isEmpty());
        assertEquals("Emma", row.getName());
        assertEquals("Stone", row.getSurname());
        assertEquals("Designer", row.getPosition());
    }

    @Test
    void generateMonthlyReport_companyNotFound_shouldThrowException() {
        when(companyRepo.findById(999L)).thenReturn(Optional.empty());
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> timesheetService.generateMonthlyReport(999L, YearMonth.of(2024, 4)));
        assertTrue(ex.getMessage().contains("Company"));
    }
}
