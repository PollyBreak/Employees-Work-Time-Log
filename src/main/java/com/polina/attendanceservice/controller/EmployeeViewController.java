package com.polina.attendanceservice.controller;

import com.polina.attendanceservice.dto.EmployeeRequest;
import com.polina.attendanceservice.dto.EmployeeStatusView;
import com.polina.attendanceservice.entity.Company;
import com.polina.attendanceservice.exception.NotFoundException;
import com.polina.attendanceservice.repository.CompanyRepository;
import com.polina.attendanceservice.service.AttendanceService;
import com.polina.attendanceservice.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/company")
public class EmployeeViewController {

    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;
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

    @GetMapping("/{id}/employees")
    public String showEmployeeRegistrationForm(@PathVariable Long id, Model model) {
        Company company = companyRepo.findById(id)
                .orElseThrow(() -> NotFoundException.forEntity("Company", id));

        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setCompanyId(company.getId());

        model.addAttribute("company", company);
        model.addAttribute("employeeRequest", employeeRequest);

        return "employee-register";
    }

    @PostMapping("/{id}/employees")
    public String registerEmployee(
            @PathVariable Long id,
            @ModelAttribute EmployeeRequest employeeRequest,
            Model model
    ) {
        try {
            employeeService.registerEmployee(employeeRequest);
            return "redirect:/company/" + id + "/employees/view?success";
        } catch (NotFoundException e) {
            Company company = companyRepo.findById(id)
                    .orElseThrow(() -> NotFoundException.forEntity("Company", id));

            model.addAttribute("company", company);
            model.addAttribute("employeeRequest", employeeRequest);
            model.addAttribute("errorMessage", e.getMessage());

            return "employee-register";
        }
    }

}
