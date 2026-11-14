package ru.practicum.microbank.accounts.mapper;

import com.google.common.base.Strings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.microbank.accounts.dto.AccountInfo;
import ru.practicum.microbank.accounts.dto.AccountInfoShort;
import ru.practicum.microbank.accounts.model.Account;
import ru.practicum.microbank.accounts.model.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface AccountMapper {

    @Mapping(target = "login", source = "user.login")
    @Mapping(target = "name", source = "user", qualifiedByName = "combineName")
    @Mapping(target = "birthday", source = "user.birthday")
    @Mapping(target = "balance", source = "balance")
    AccountInfo toDto(Account account);

    @Named("combineName")
    default String combineName(User user) {
        if (user == null) {
            return null;
        }
        return combineName(user.getFirstName(), user.getLastName());
    }

    default String combineName(String firstName, String lastName) {
        if (Strings.isNullOrEmpty(firstName) && Strings.isNullOrEmpty(lastName)) {
            return null;
        }
        if (Strings.isNullOrEmpty(firstName)) {
            return lastName;
        }
        if (Strings.isNullOrEmpty(lastName)) {
            return firstName;
        }
        return firstName + " " + lastName;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "accountInfoShort")
    @Mapping(target = "balance", constant = "0")
    Account toEntity(AccountInfoShort accountInfoShort);

    @Mapping(target = "login", source = "user.login")
    @Mapping(target = "name", source = "user", qualifiedByName = "combineName")
    AccountInfoShort toDtoShort(Account account);

    List<AccountInfoShort> toDtoShortList(List<Account> accounts);
}
