package com.polina.attendanceservice.service;

import com.polina.attendanceservice.entity.BusinessTrip;
import com.polina.attendanceservice.entity.Employee;
import com.polina.attendanceservice.repository.BusinessTripRepository;
import com.polina.attendanceservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BusinessTripService {

    private final EmployeeRepository employeeRepo;
    private final BusinessTripRepository tripRepo;

    public void registerTrip(Long companyId, String email) {
        Employee employee = employeeRepo.findByEmail(email)
                .filter(e -> e.getCompany().getId().equals(companyId))
                .orElseThrow(() -> new RuntimeException("Email не найден в компании"));

        LocalDate today = LocalDate.now();
        if (tripRepo.existsByEmployeeAndDate(employee, today)) {
            throw new RuntimeException("Вы уже отметились сегодня");
        }

        BusinessTrip trip = new BusinessTrip();
        trip.setEmployee(employee);
        trip.setDate(today);

        tripRepo.save(trip);
    }
}
