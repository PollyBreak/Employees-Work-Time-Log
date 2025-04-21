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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.polina.attendanceservice.util.DateRangeUtil.getStartTime;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final EmployeeRepository employeeRepo;
    private final AttendanceRepository attendanceRepo;
    private final CompanyRepository companyRepository;

    public String processScan(Long companyId, String macAddress) {
        Optional<Employee> optionalEmployee = employeeRepo.findByMacAddress(macAddress)
                .filter(e -> e.getCompany().getId().equals(companyId));

        if (optionalEmployee.isEmpty()) {
            return null;
        }

        Employee employee = optionalEmployee.get();
        Optional<Attendance> last = attendanceRepo.findTopByEmployeeOrderByTimestampDesc(employee);
        boolean newStatus = last.map(a -> !a.isAtWork()).orElse(true);

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setAtWork(newStatus);
        attendance.setTimestamp(LocalDateTime.now());
        attendanceRepo.save(attendance);

        return newStatus ? "Welcome, you're now AT WORK!" : "You have LEFT WORK!";
    }




    public Boolean checkEmployeeStatus(Long employeeId) {
        Employee emp = employeeRepo.findById(employeeId)
                .orElseThrow(() -> NotFoundException.forEntity("Employee", employeeId));
        return attendanceRepo.findTopByEmployeeOrderByTimestampDesc(emp)
                .map(Attendance::isAtWork)
                .orElse(false);
    }

    public List<AttendanceRecordResponse> getEmployeeAttendance(Long employeeId, RangeType range) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> NotFoundException.forEntity("Employee", employeeId));

        LocalDateTime start = getStartTime(range);

        return attendanceRepo.findByEmployeeAndTimestampAfterOrderByTimestampDesc(employee, start)
                .stream()
                .map(a -> new AttendanceRecordResponse(
                        employee.getId(),
                        employee.getName() + " " + employee.getSurname(),
                        a.isAtWork(),
                        a.getTimestamp()
                ))
                .toList();
    }

    public List<AttendanceRecordResponse> getCompanyAttendance(Long companyId, RangeType range) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> NotFoundException.forEntity("Company", companyId));

        LocalDateTime start = getStartTime(range);

        return employeeRepo.findAll().stream()
                .filter(e -> e.getCompany().getId().equals(companyId))
                .flatMap(e -> attendanceRepo.findByEmployeeAndTimestampAfterOrderByTimestampDesc(e, start).stream()
                        .map(a -> new AttendanceRecordResponse(
                                e.getId(),
                                e.getName() + " " + e.getSurname(),
                                a.isAtWork(),
                                a.getTimestamp()
                        )))
                .toList();
    }


    public List<EmployeeStatusView> getCompanyEmployeesWithStatus(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> NotFoundException.forEntity("Company", companyId));

        return employeeRepo.findAll().stream()
                .filter(e -> e.getCompany().getId().equals(companyId))
                .map(e -> {
                    boolean atWork = attendanceRepo.findTopByEmployeeOrderByTimestampDesc(e)
                            .map(Attendance::isAtWork)
                            .orElse(false);

                    return new EmployeeStatusView(
                            e.getId(),
                            e.getName(),
                            e.getSurname(),
                            e.getRoom(),
                            e.getPhone(),
                            e.getPosition(),
                            atWork
                    );
                })
                .toList();
    }


}
