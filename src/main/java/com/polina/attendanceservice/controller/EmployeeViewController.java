package com.polina.attendanceservice.controller;

import com.polina.attendanceservice.dto.EmployeeStatusView;
import com.polina.attendanceservice.entity.Company;
import com.polina.attendanceservice.exception.NotFoundException;
import com.polina.attendanceservice.repository.CompanyRepository;
import com.polina.attendanceservice.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/company")
public class EmployeeViewController {

    private final AttendanceService attendanceService;
    private final CompanyRepository companyRepo;

    @GetMapping("/{id}/employees/view")
    public String viewCompanyEmployees(@PathVariable Long id, Model model) {
        Company company = companyRepo.findById(id)
                .orElseThrow(() -> NotFoundException.forEntity("Company", id));

        List<EmployeeStatusView> employees = attendanceService.getCompanyEmployeesWithStatus(id);

        model.addAttribute("company", company);
        model.addAttribute("employees", employees);
        return "employee-view";
    }
}
