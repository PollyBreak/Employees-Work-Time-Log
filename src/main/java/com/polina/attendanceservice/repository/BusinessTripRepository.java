package com.polina.attendanceservice.repository;

import com.polina.attendanceservice.entity.BusinessTrip;
import com.polina.attendanceservice.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BusinessTripRepository extends JpaRepository<BusinessTrip, Long> {
    boolean existsByEmployeeAndDate(Employee employee, LocalDate date);
    List<BusinessTrip> findByEmployeeAndDateBetween(Employee employee, LocalDate start, LocalDate end);
}
