package com.polina.attendanceservice.service;

import com.polina.attendanceservice.dto.EmployeeRequest;
import com.polina.attendanceservice.entity.Company;
import com.polina.attendanceservice.entity.Employee;
import com.polina.attendanceservice.exception.NotFoundException;
import com.polina.attendanceservice.repository.CompanyRepository;
import com.polina.attendanceservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepo;
    private final CompanyRepository companyRepo;

    public Employee registerEmployee(EmployeeRequest request) {
        Company company = companyRepo.findById(request.getCompanyId())
                .orElseThrow(() -> NotFoundException.forEntity("Company", request.getCompanyId()));
        employeeRepo.findByEmail(request.getEmail()).ifPresent(e -> {
            throw new NotFoundException("Email " + request.getEmail() + " is already in use by employee ID " + e.getId());
        });

        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setSurname(request.getSurname());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setRoom(request.getRoom());
        employee.setPosition(request.getPosition());
        employee.setCompany(company);

        return employeeRepo.save(employee);
    }



    public List<Employee> getAllEmployees() {
        return employeeRepo.findAll();
    }
}
