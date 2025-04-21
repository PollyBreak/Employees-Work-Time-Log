package com.polina.attendanceservice.controller;

import com.polina.attendanceservice.dto.ScanRequest;
import com.polina.attendanceservice.entity.Company;
import com.polina.attendanceservice.repository.CompanyRepository;
import com.polina.attendanceservice.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AttendanceWebController {
    private final AttendanceService attendanceService;
    private final CompanyRepository companyRepo;

    @GetMapping("/company/{id}/attendance")
    public String showForm(@PathVariable Long id, Model model) {
        Company company = companyRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        ScanRequest scanRequest = new ScanRequest();
        scanRequest.setCompanyId(id);

        model.addAttribute("company", company);
        model.addAttribute("scanRequest", scanRequest);
        return "attendance-form";
    }


    @PostMapping("/company/scan")
    public String handleForm(@ModelAttribute ScanRequest scanRequest, Model model) {
        String result = attendanceService.processScan(scanRequest.getCompanyId(), scanRequest.getMacAddress());

        Company company = companyRepo.findById(scanRequest.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        model.addAttribute("company", company);
        model.addAttribute("scanRequest", scanRequest);

        if (result == null) {
            model.addAttribute("error", "This MAC is not registered under this company. Please check your input.");
        } else {
            model.addAttribute("message", result);
        }

        return "attendance-form";
    }


}
