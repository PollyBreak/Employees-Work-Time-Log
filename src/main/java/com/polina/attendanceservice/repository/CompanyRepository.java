package com.polina.attendanceservice.repository;

import com.polina.attendanceservice.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {}