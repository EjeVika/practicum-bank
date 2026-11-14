package ru.practicum.microbank.accounts.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uq_login", columnNames = "login"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 64)
    private String login;
    @Column(name = "first_name", nullable = false, length = 64)
    private String firstName;
    @Column(name = "last_name", nullable = false, length = 64)
    private String lastName;
    @Column(name = "birthday")
    private LocalDate birthday;
}
