package com.symplifica.backend.dto;
import com.symplifica.backend.entity.Employee;
import java.time.LocalDateTime;
import java.util.UUID;

public record EmployeeResponse(
    UUID id,
    String email,
    String firstName,
    String lastName,
    String jobTitle,
    String city,
    String street,
    String state,
    String country,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static EmployeeResponse fromEntity(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getEmail(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getJobTitle(),
                employee.getCity(),
                employee.getStreet(),
                employee.getState(),
                employee.getCountry(),
                employee.getCreatedAt(),
                employee.getUpdatedAt());
    }
}