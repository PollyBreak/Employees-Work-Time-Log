package com.polina.attendanceservice.controller.rest;

import com.polina.attendanceservice.dto.AttendanceRecordResponse;
import com.polina.attendanceservice.dto.CompanyRequest;
import com.polina.attendanceservice.dto.CompanyStatusResponse;
import com.polina.attendanceservice.entity.Company;
import com.polina.attendanceservice.service.AttendanceService;
import com.polina.attendanceservice.service.CompanyService;
import com.polina.attendanceservice.util.RangeType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/company")
public class CompanyController {
    private final CompanyService companyService;
    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<Company> create(@RequestBody CompanyRequest request) {
        return ResponseEntity.ok(companyService.createCompany(request));
    }

    @GetMapping
    public ResponseEntity<List<Company>> getAll() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/{id}/employees/statuses")
    public ResponseEntity<CompanyStatusResponse> getWithStatus(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompanyWithStatuses(id));
    }



    @GetMapping("/{id}/attendance")
    public ResponseEntity<List<AttendanceRecordResponse>> getCompanyHistory(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "ALL") String range
    ) {
        RangeType rangeType = RangeType.valueOf(range.toUpperCase());
        return ResponseEntity.ok(attendanceService.getCompanyAttendance(id, rangeType));
    }

}
