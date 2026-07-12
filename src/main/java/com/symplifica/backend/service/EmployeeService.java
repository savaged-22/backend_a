package com.symplifica.backend.service;

import com.symplifica.backend.dto.EmployeeRequest;
import com.symplifica.backend.dto.EmployeeResponse;
import com.symplifica.backend.entity.Employee;
import com.symplifica.backend.event.EmployeeCreatedEvent;
import com.symplifica.backend.repository.EmployeeRepository;
import com.symplifica.backend.exception.DuplicateEmailException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService{
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;
    
    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAll(){
        return employeeRepository.findAll()
            .stream()
            .map(EmployeeResponse::fromEntity)
            .toList();
    }


    @Transactional
    public EmployeeResponse create(EmployeeRequest request){
        if (employeeRepository.existsByEmail(request.email())){
            throw new DuplicateEmailException("Ya existe empleado con ese Email.");
        }

       Employee employee=Employee.builder()
            .email(request.email())
            .passwordHash(passwordEncoder.encode(request.password()))
            .firstName(request.firstName())
            .lastName(request.lastName())
            .jobTitle(request.jobTitle())
            .city(request.city())
            .street(request.street())
            .state(request.state())
            .country(request.country())
            .build();

        Employee saved=employeeRepository.save(employee);

        applicationEventPublisher.publishEvent(EmployeeCreatedEvent.of(
                saved.getId(),
                saved.getEmail(),
                saved.getFirstName(),
                saved.getLastName(),
                saved.getJobTitle(),
                saved.getCity(),
                saved.getStreet(),
                saved.getState(),
                saved.getCountry()
        ));

        return EmployeeResponse.fromEntity(saved);
    }


}
