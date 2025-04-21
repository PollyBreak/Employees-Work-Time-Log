package com.polina.attendanceservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;

    @Column(unique = true, nullable = false)
    private String macAddress;
    private String phone;
    private String room;
    private String position;

    @ManyToOne
    private Company company;
}
