package com.symplifica.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symplifica.backend.dto.EmployeeRequest;
import com.symplifica.backend.dto.EmployeeResponse;
import com.symplifica.backend.exception.DuplicateEmailException;
import com.symplifica.backend.service.EmployeeService;
import com.symplifica.backend.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void createEmployee_ShouldReturn201_WhenValidRequest() throws Exception {
        EmployeeRequest request = new EmployeeRequest("test@test.com", "password", "John", "Doe", "Dev", "City", "Street", "State", "Country");
        EmployeeResponse response = new EmployeeResponse(UUID.randomUUID(), "test@test.com", "John", "Doe", "Dev", "City", "Street", "State", "Country", null, null);

        when(employeeService.create(any(EmployeeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void createEmployee_ShouldReturn400_WhenValidationFails() throws Exception {
        // Petición inválida: sin email
        EmployeeRequest request = new EmployeeRequest("", "password", "John", "Doe", "Dev", "City", "Street", "State", "Country");

        mockMvc.perform(post("/api/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEmployee_ShouldReturn409_WhenEmailExists() throws Exception {
        EmployeeRequest request = new EmployeeRequest("test@test.com", "password", "John", "Doe", "Dev", "City", "Street", "State", "Country");

        when(employeeService.create(any(EmployeeRequest.class))).thenThrow(new DuplicateEmailException("Ya existe empleado"));

        mockMvc.perform(post("/api/employees")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    void listEmployees_ShouldReturn200_WhenAuthenticated() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk());
    }

    @Test
    void listEmployees_ShouldReturn401_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isUnauthorized());
    }
}
