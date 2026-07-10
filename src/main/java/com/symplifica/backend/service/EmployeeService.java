package com.symplifica.backend.service;

import com.symplifica.backend.dto.EmployeeRequest;
import com.symplifica.backend.dto.EmployeeResponse;
import com.symplifica.backend.entity.Employee;
import com.symplifica.backend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService{
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    
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
            throw new DuplicateEmailException("Ya existe empleado con ese Email.")
        }

       Employee employee=Employee.builder()
            .email(request.email())
            .passwordHash(passwordEncoder.encode(request.password()))
            .firstName(request.firstName())
            .lastName(request.lastName())
            .jobTitle(request.jobTitle())
            .city(request.city())
            .build();

        Employee saved=employeeRepository.save(employee);
        return EmployeeResponse.fromEntity(saved);
    }


}
