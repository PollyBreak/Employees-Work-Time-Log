package com.polina.attendanceservice.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException forEntity(String entity, Long id) {
        return new NotFoundException(entity + " with ID " + id + " not found.");
    }
}
