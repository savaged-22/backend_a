package com.symplifica.backend.service;

import com.symplifica.backend.dto.EmployeeRequest;
import com.symplifica.backend.dto.EmployeeResponse;
import com.symplifica.backend.entity.Employee;
import com.symplifica.backend.event.EmployeeCreatedEvent;
import com.symplifica.backend.exception.DuplicateEmailException;
import com.symplifica.backend.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void create_ShouldThrowDuplicateEmailException_WhenEmailExists() {
        EmployeeRequest request = new EmployeeRequest("test@test.com", "password", "John", "Doe", "Dev", "City", "Street", "State", "Country");
        when(employeeRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.create(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Ya existe empleado con ese Email");

        verify(employeeRepository, never()).save(any());
        verify(applicationEventPublisher, never()).publishEvent(any());
    }

    @Test
    void create_ShouldHashPasswordAndPublishEvent_WhenValidRequest() {
        EmployeeRequest request = new EmployeeRequest("test@test.com", "plainPassword", "John", "Doe", "Dev", "City", "Street", "State", "Country");
        
        when(employeeRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");

        Employee savedEmployee = Employee.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .passwordHash("hashedPassword")
                .firstName("John")
                .lastName("Doe")
                .jobTitle("Dev")
                .city("City")
                .street("Street")
                .state("State")
                .country("Country")
                .build();

        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        EmployeeResponse response = employeeService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("test@test.com");

        // Capturar y verificar el empleado que se guardó
        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(employeeCaptor.capture());
        Employee capturedEmployee = employeeCaptor.getValue();

        assertThat(capturedEmployee.getPasswordHash()).isEqualTo("hashedPassword");
        assertThat(capturedEmployee.getPasswordHash()).isNotEqualTo("plainPassword");

        // Verificar la publicación del evento
        ArgumentCaptor<EmployeeCreatedEvent> eventCaptor = ArgumentCaptor.forClass(EmployeeCreatedEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        EmployeeCreatedEvent publishedEvent = eventCaptor.getValue();

        assertThat(publishedEvent.employeeId()).isEqualTo(savedEmployee.getId());
        assertThat(publishedEvent.email()).isEqualTo("test@test.com");
    }
}
