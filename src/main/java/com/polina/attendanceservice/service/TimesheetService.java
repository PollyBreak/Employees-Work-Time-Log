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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimesheetService {

    private final EmployeeRepository employeeRepo;
    private final CompanyRepository companyRepository;
    private final AttendanceRepository attendanceRepo;
    private final BusinessTripRepository businessTripRepo;

    public List<MonthlyTimesheetRow> generateMonthlyReport(Long companyId, YearMonth month) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> NotFoundException.forEntity("Company", companyId));

        List<Employee> employees = employeeRepo.findAll().stream()
                .filter(e -> e.getCompany().getId().equals(companyId))
                .toList();

        return employees.stream()
                .map(e -> buildRowForEmployee(e, month))
                .toList();
    }

    private MonthlyTimesheetRow buildRowForEmployee(Employee employee, YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();

        List<BusinessTrip> trips = businessTripRepo.findByEmployeeAndDateBetween(employee, start, end);
        Set<LocalDate> tripDates = trips.stream()
                .map(BusinessTrip::getDate)
                .collect(Collectors.toSet());

        List<Attendance> attendances = attendanceRepo
                .findByEmployeeAndTimestampBetweenOrderByTimestamp(employee, start.atStartOfDay(), end.plusDays(1).atStartOfDay());

        Map<LocalDate, List<Attendance>> byDate = attendances.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getTimestamp().toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        Map<LocalDate, Duration> workedPerDay = new LinkedHashMap<>();
        Duration total = Duration.ZERO;

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            Duration totalDay = Duration.ZERO;

            if (tripDates.contains(date)) {
                totalDay = Duration.ofHours(8);
            } else {
                List<Attendance> records = new ArrayList<>(byDate.getOrDefault(date, List.of()));
                records.sort(Comparator.comparing(Attendance::getTimestamp));

                LocalDateTime checkIn = null;
                for (Attendance a : records) {
                    if (a.isAtWork()) {
                        checkIn = a.getTimestamp();
                    } else if (!a.isAtWork() && checkIn != null) {
                        Duration worked = Duration.between(checkIn, a.getTimestamp());
                        if (!worked.isNegative() && !worked.isZero()) {
                            totalDay = totalDay.plus(worked);
                        }
                        checkIn = null;
                    }
                }
            }

            if (!totalDay.isZero()) {
                workedPerDay.put(date, totalDay);
                total = total.plus(totalDay);
            }
        }

        return new MonthlyTimesheetRow(
                employee.getId(),
                employee.getName(),
                employee.getSurname(),
                employee.getPosition(),
                null,
                workedPerDay,
                total
        );
    }
}
