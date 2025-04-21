package com.polina.attendanceservice.dto;

import lombok.Data;

@Data
public class ScanRequest {
    private Long companyId;
    private String macAddress;
}
