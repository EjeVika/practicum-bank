package ru.practicum.microbank.accounts.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.microbank.accounts.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class NameUtilsTest {

    // ==========================================
    // Тесты для extractFirstName
    // ==========================================

    @Test
    @DisplayName("extractFirstName должен возвращать первое слово из строки")
    void extractFirstName_WithValidName_ShouldReturnFirstName() {
        assertThat(NameUtils.extractFirstName("Иванов Иван")).isEqualTo("Иванов");
        assertThat(NameUtils.extractFirstName("Иванов Иван Петрович")).isEqualTo("Иванов");
    }

    @Test
    @DisplayName("extractFirstName должен возвращать null для null или пустой/пробельной строки")
    void extractFirstName_WithNullOrBlank_ShouldReturnNull() {
        assertThat(NameUtils.extractFirstName(null)).isNull();
        assertThat(NameUtils.extractFirstName("")).isNull();
        assertThat(NameUtils.extractFirstName("   ")).isNull();
    }

    @Test
    @DisplayName("extractFirstName должен корректно обрабатывать строку с одним словом и лишними пробелами")
    void extractFirstName_WithSingleWordAndSpaces_ShouldReturnTrimmedWord() {
        assertThat(NameUtils.extractFirstName("  Иванов  ")).isEqualTo("Иванов");
    }

    // ==========================================
    // Тесты для extractLastName
    // ==========================================

    @Test
    @DisplayName("extractLastName должен возвращать всё, что идет после первого слова")
    void extractLastName_WithValidName_ShouldReturnLastName() {
        assertThat(NameUtils.extractLastName("Иванов Иван")).isEqualTo("Иван");
        assertThat(NameUtils.extractLastName("Иванов Иван Петрович")).isEqualTo("Иван Петрович");
    }

    @Test
    @DisplayName("extractLastName должен возвращать null, если в строке только одно слово")
    void extractLastName_WithSingleWord_ShouldReturnNull() {
        assertThat(NameUtils.extractLastName("Иванов")).isNull();
    }

    @Test
    @DisplayName("extractLastName должен возвращать null для null или пустой/пробельной строки")
    void extractLastName_WithNullOrBlank_ShouldReturnNull() {
        assertThat(NameUtils.extractLastName(null)).isNull();
        assertThat(NameUtils.extractLastName("")).isNull();
        assertThat(NameUtils.extractLastName("   ")).isNull();
    }

    // ==========================================
    // Тесты для combineName(User)
    // ==========================================

    @Test
    @DisplayName("combineName(User) должен возвращать null, если передан null")
    void combineName_WithNullUser_ShouldReturnNull() {
        assertThat(NameUtils.combineName((User) null)).isNull();
    }

    @Test
    @DisplayName("combineName(User) должен корректно объединять имя и фамилию пользователя")
    void combineName_WithValidUser_ShouldReturnCombinedName() {
        User user = User.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .build();

        assertThat(NameUtils.combineName(user)).isEqualTo("Иван Иванов");
    }

    // ==========================================
    // Тесты для combineName(String, String)
    // ==========================================

    @Test
    @DisplayName("combineName(String, String) должен объединять два непустых имени через пробел")
    void combineName_WithBothNamesPresent_ShouldReturnCombined() {
        assertThat(NameUtils.combineName("Иван", "Иванов")).isEqualTo("Иван Иванов");
    }

    @Test
    @DisplayName("combineName(String, String) должен возвращать только фамилию, если имя null или пустое")
    void combineName_WithNullOrEmptyFirstName_ShouldReturnLastName() {
        assertThat(NameUtils.combineName(null, "Иванов")).isEqualTo("Иванов");
        assertThat(NameUtils.combineName("", "Иванов")).isEqualTo("Иванов");
    }

    @Test
    @DisplayName("combineName(String, String) должен возвращать только имя, если фамилия null или пустая")
    void combineName_WithNullOrEmptyLastName_ShouldReturnFirstName() {
        assertThat(NameUtils.combineName("Иван", null)).isEqualTo("Иван");
        assertThat(NameUtils.combineName("Иван", "")).isEqualTo("Иван");
    }

    @Test
    @DisplayName("combineName(String, String) должен возвращать null, если оба значения null или пустые")
    void combineName_WithBothNamesNullOrBlank_ShouldReturnNull() {
        assertThat(NameUtils.combineName(null, null)).isNull();
        assertThat(NameUtils.combineName("", "")).isNull();
    }
}
