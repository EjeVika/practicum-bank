package ru.practicum.microbank.accounts.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.microbank.accounts.AbstractIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class FlywayMigrationIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DisplayName("Flyway миграции должны успешно применяться к тестовой БД")
    void flywayMigrations_ShouldBeAppliedSuccessfully() {
        assertThat(accountRepository.getAccountList().size() == 3);
    }
}
