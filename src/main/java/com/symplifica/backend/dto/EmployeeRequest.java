package com.symplifica.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmployeeRequest(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        @NotBlank(message = "El nombre es obligatorio")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        String lastName,

        @NotBlank(message = "El cargo es obligatorio")
        String jobTitle,

        @NotBlank(message = "La ciudad es obligatoria")
        String city,

        @NotBlank(message = "La calle es obligatoria")
        String street,

        @NotBlank(message = "El departamento es obligatorio")
        String state,

        @NotBlank(message = "El pais es obligatorio")
        String country
)   {}
