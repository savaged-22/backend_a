package com.symplifica.backend.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeEventPublisher {

    private final KafkaTemplate<String, EmployeeCreatedEvent> kafkaTemplate;

    @Value("${app.kafka.employee-events-topic}")
    private String topic;

    public void publishEmployeeCreated(EmployeeCreatedEvent event) {
        kafkaTemplate.send(topic, event.employeeId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Error publicando employee.created para {}: {}", event.employeeId(), ex.getMessage(), ex);
                    } else {
                        log.info("Evento employee.created publicado para empleado {}", event.employeeId());
                    }
                });
    }
}
