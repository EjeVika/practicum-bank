package ru.practicum.microbank.accounts.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.practicum.microbank.accounts.AbstractIntegrationTest;
import ru.practicum.microbank.accounts.model.Account;
import ru.practicum.microbank.accounts.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AccountRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "accounts", "users");
    }

    @Test
    @DisplayName("Должен сохранять и находить аккаунт по логину пользователя")
    void shouldSaveAndFindAccountByLogin() {
        User user = User.builder()
                .login("testov")
                .firstName("Test")
                .lastName("Testov")
                .birthday(LocalDate.of(1990, 1, 15))
                .build();

        Account account = Account.builder()
                .user(user)
                .balance(new BigDecimal("1000.00"))
                .build();

        Account saved = accountRepository.saveAndFlush(account);
        Optional<Account> found = accountRepository.findByLogin("testov");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getUser().getFirstName()).isEqualTo("Test");
        assertThat(found.get().getBalance()).isEqualByComparingTo("1000.00");
    }

    @Test
    @DisplayName("Должен возвращать пустой Optional, если логин не найден")
    void shouldReturnEmptyWhenLoginNotFound() {
        Optional<Account> found = accountRepository.findByLogin("non-existent");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Должен возвращать список краткой информации о счетах")
    void shouldReturnAccountInfoShortList() {
        // Arrange - создаем 3 аккаунта
        for (int i = 1; i <= 3; i++) {
            User user = User.builder()
                    .login("user" + i)
                    .firstName("First" + i)
                    .lastName("Last" + i)
                    .birthday(LocalDate.now())
                    .build();
            accountRepository.saveAndFlush(Account.builder()
                    .user(user)
                    .balance(BigDecimal.valueOf(100 * i))
                    .build());
        }

        // Act
        var list = accountRepository.getAccountList();

        // Assert
        assertThat(list).hasSize(3);
    }

}