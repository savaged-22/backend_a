package com.symplifica.backend.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record EmployeeCreatedEvent(
        String eventType,
        UUID employeeId,
        String email,
        String firstName,
        String lastName,
        String jobTitle,
        String city,
        String street,
        String state,
        String country,
        LocalDateTime occurredAt
) {
    public static EmployeeCreatedEvent of(
            UUID employeeId,
            String email,
            String firstName,
            String lastName,
            String jobTitle,
            String city,
            String street,
            String state,
            String country
    ) {
        return new EmployeeCreatedEvent(
                "employee.created",
                employeeId,
                email,
                firstName,
                lastName,
                jobTitle,
                city,
                street,
                state,
                country,
                LocalDateTime.now()
        );
    }
}
