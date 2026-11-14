package ru.practicum.microbank.accounts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.microbank.accounts.model.Account;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("""
           select a
           from Account a
           join fetch a.user u
           where u.login = :login
           """)
    Optional<Account> findByLogin(@Param("login") String login);

    @Query("""
            select a
            from Account a
            join fetch a.user
            order by a.id
            """)
    List<Account> getAccountList();


}
