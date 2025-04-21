package com.polina.attendanceservice.repository;

import com.polina.attendanceservice.entity.Attendance;
import com.polina.attendanceservice.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findTopByEmployeeOrderByTimestampDesc(Employee employee);

    List<Attendance> findByEmployeeAndTimestampAfterOrderByTimestampDesc(Employee employee,
                                                                         LocalDateTime from);

    List<Attendance> findByEmployeeAndTimestampBetweenOrderByTimestamp(Employee employee,
                                                                       LocalDateTime start,
                                                                       LocalDateTime end);

}