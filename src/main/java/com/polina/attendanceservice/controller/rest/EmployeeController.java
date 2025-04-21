package com.polina.attendanceservice.controller.rest;

import com.polina.attendanceservice.dto.AttendanceRecordResponse;
import com.polina.attendanceservice.dto.EmployeeRequest;
import com.polina.attendanceservice.entity.Employee;
import com.polina.attendanceservice.service.AttendanceService;
import com.polina.attendanceservice.service.EmployeeService;
import com.polina.attendanceservice.util.RangeType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employee")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<Employee> register(@RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.registerEmployee(request));
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAll() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}/attendance")
    public ResponseEntity<List<AttendanceRecordResponse>> getEmployeeHistory(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "ALL") String range
    ) {
        RangeType rangeType = RangeType.valueOf(range.toUpperCase());
        return ResponseEntity.ok(attendanceService.getEmployeeAttendance(id, rangeType));
    }
}
