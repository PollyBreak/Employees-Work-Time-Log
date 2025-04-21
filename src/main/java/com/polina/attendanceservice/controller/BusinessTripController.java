package com.polina.attendanceservice.controller;

import com.polina.attendanceservice.repository.EmployeeRepository;
import com.polina.attendanceservice.service.BusinessTripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/company/{companyId}/business-trip")
public class BusinessTripController {

    private final BusinessTripService tripService;
    private final EmployeeRepository employeeRepo;

    @GetMapping
    public String tripForm(@PathVariable Long companyId, Model model) {
        model.addAttribute("companyId", companyId);
        return "business-trip-form";
    }

    @PostMapping
    public String handleTripSubmit(@PathVariable Long companyId, @RequestParam String macAddress, Model model) {
        try {
            tripService.registerTrip(companyId, macAddress);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("companyId", companyId);
        return "business-trip-form";
    }
}
