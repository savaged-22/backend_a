package com.symplifica.backend.controller;

import com.symplifica.backend.dto.EmployeeRequest;
import com.symplifica.backend.dto.EmployeeResponse;
import com.symplifica.backend.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService empoyeeService;

    @GetMapping 
    public ResponseEntity<List<EmployeeResponse>> listEmployees(){
        return ResponseEntity.ok(employeeService.findAll());
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request){
        EmployeeResponse created = employeeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

}