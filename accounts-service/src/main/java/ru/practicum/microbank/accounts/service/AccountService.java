package ru.practicum.microbank.accounts.service;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.microbank.accounts.dto.AccountInfo;
import ru.practicum.microbank.accounts.dto.AccountInfoShort;
import ru.practicum.microbank.accounts.dto.UpdateAccountRequest;
import ru.practicum.microbank.accounts.exception.AccountNotFoundException;
import ru.practicum.microbank.accounts.mapper.AccountMapper;
import ru.practicum.microbank.accounts.mapper.UserMapper;
import ru.practicum.microbank.accounts.model.Account;
import ru.practicum.microbank.accounts.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final UserMapper userMapper;

    public AccountService(AccountRepository accountRepository, AccountMapper accountMapper, UserMapper userMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public AccountInfo findByLogin(String login) {
        Preconditions.checkArgument(Strings.isNotBlank(login), "Логин пуст");

        return accountRepository.findByLogin(login)
                .map(accountMapper::toDto)
                .orElseThrow(() -> new AccountNotFoundException(login));
    }

    @Transactional
    public AccountInfo createAccountAndUser(String login, AccountInfoShort accountInfoShort) {
        Preconditions.checkArgument(Strings.isNotBlank(login), "Логин пуст");
        AccountInfo existedAccount = null;
        try {
            existedAccount = findByLogin(accountInfoShort.login());
        } catch (AccountNotFoundException e) {
            log.info("Логин {} свободен, создаем нового пользователя... ", accountInfoShort.login());
        }
        if (existedAccount != null) {
            log.warn("Логин {} уже занят, новый пользователь создан не будет ", accountInfoShort.login());
            return existedAccount;
        }
        Account account = accountMapper.toEntity(accountInfoShort);
        // Создаем пользователя с нулевым балансом
        account.setBalance(BigDecimal.ZERO);
        Account saved = accountRepository.saveAndFlush(account);
        log.info("Создан пользователь и ЛС account_id={}, user_id={}, login={}",
                saved.getId(), saved.getUser().getId(), accountInfoShort.login());
        return accountMapper.toDto(saved);
    }

    @Transactional
    public AccountInfo updateAccount(String login, UpdateAccountRequest updateUserInfo) {
        Preconditions.checkArgument(Strings.isNotBlank(login), "Логин пуст");

        Account account = accountRepository.findByLogin(login)
                .orElseThrow(() -> new AccountNotFoundException(login));

        log.info("Информация о пользователе {} обновляется на {}", account.getUser(), updateUserInfo);
        userMapper.updateUserFromDto(updateUserInfo, account.getUser());
        return accountMapper.toDto(account);
    }

    @Transactional(readOnly = true)
    public List<AccountInfoShort> getAccountInfoShorts() {
        return accountMapper.toDtoShortList(accountRepository.getAccountList());
    }
}
