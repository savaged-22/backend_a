package com.symplifica.backend.event;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class EmployeeCreatedEventListenerTest {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @MockBean
    private EmployeeEventPublisher employeeEventPublisher;

    @Test
    void onEmployeeCreated_ShouldPublishToKafka_WhenTransactionCommits() {
        EmployeeCreatedEvent event = EmployeeCreatedEvent.of(
                UUID.randomUUID(), "test@test.com", "John", "Doe", "Dev", "City", "Street", "State", "Country"
        );

        // Simulamos una transacción que hace commit exitoso
        transactionTemplate.executeWithoutResult(status -> {
            applicationEventPublisher.publishEvent(event);
            // Dentro de la transacción, aún no se debe haber llamado al publisher de Kafka
            verify(employeeEventPublisher, never()).publishEmployeeCreated(any());
        });

        // Después del commit, el listener debió ejecutarse
        verify(employeeEventPublisher).publishEmployeeCreated(event);
    }

    @Test
    void onEmployeeCreated_ShouldNotPublishToKafka_WhenTransactionRollsBack() {
        EmployeeCreatedEvent event = EmployeeCreatedEvent.of(
                UUID.randomUUID(), "test@test.com", "John", "Doe", "Dev", "City", "Street", "State", "Country"
        );

        try {
            // Simulamos una transacción que falla y hace rollback
            transactionTemplate.executeWithoutResult(status -> {
                applicationEventPublisher.publishEvent(event);
                status.setRollbackOnly(); // Forzamos el rollback
            });
        } catch (Exception ignored) {
        }

        // Después del rollback, el listener no debió ejecutarse
        verify(employeeEventPublisher, never()).publishEmployeeCreated(any());
    }
}
