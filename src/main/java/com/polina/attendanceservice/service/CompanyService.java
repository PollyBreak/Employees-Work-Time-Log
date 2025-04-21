package com.polina.attendanceservice.service;

import com.polina.attendanceservice.dto.CompanyStatusResponse;
import com.polina.attendanceservice.dto.EmployeeStatusResponse;
import com.polina.attendanceservice.dto.CompanyRequest;
import com.polina.attendanceservice.entity.Attendance;
import com.polina.attendanceservice.entity.Company;
import com.polina.attendanceservice.entity.Employee;
import com.polina.attendanceservice.exception.NotFoundException;
import com.polina.attendanceservice.repository.AttendanceRepository;
import com.polina.attendanceservice.repository.CompanyRepository;
import com.polina.attendanceservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepo;
    private final EmployeeRepository employeeRepo;
    private final AttendanceRepository attendanceRepo;

    public Company createCompany(CompanyRequest request) {
        Company company = new Company();
        company.setName(request.getName());
        return companyRepo.save(company);
    }

    public List<Company> getAllCompanies() {
        return companyRepo.findAll();
    }

    public CompanyStatusResponse getCompanyWithStatuses(Long companyId) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> NotFoundException.forEntity("Company", companyId));

        List<Employee> employees = employeeRepo.findAll().stream()
                .filter(e -> e.getCompany().getId().equals(companyId))
                .toList();

        List<EmployeeStatusResponse> employeeStatuses = employees.stream()
                .map(emp -> {
                    boolean atWork = attendanceRepo
                            .findTopByEmployeeOrderByTimestampDesc(emp)
                            .map(Attendance::isAtWork)
                            .orElse(false);

                    return new EmployeeStatusResponse(emp.getId(), emp.getName(), emp.getSurname(), atWork);
                })
                .toList();

        return new CompanyStatusResponse(company.getId(), company.getName(), employeeStatuses);
    }
}
