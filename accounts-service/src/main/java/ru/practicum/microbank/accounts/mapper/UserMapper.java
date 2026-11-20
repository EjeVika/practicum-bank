package ru.practicum.microbank.accounts.mapper;

import org.mapstruct.*;
import ru.practicum.microbank.accounts.dto.AccountInfoShort;
import ru.practicum.microbank.accounts.dto.UpdateAccountRequest;
import ru.practicum.microbank.accounts.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "login", source = "login")
    @Mapping(target = "firstName", expression = "java(NameUtils.extractFirstName(accountInfoShort.name()))")
    @Mapping(target = "lastName", expression = "java(NameUtils.extractLastName(accountInfoShort.name()))")
    @Mapping(target = "birthday", ignore = true)
    User toEntity(AccountInfoShort accountInfoShort);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "login", ignore = true)
    @Mapping(target = "firstName", expression = "java(NameUtils.extractFirstName(dto.name()))")
    @Mapping(target = "lastName", expression = "java(NameUtils.extractLastName(dto.name()))")
    @Mapping(target = "birthday", source = "birthday")
    void updateUserFromDto(UpdateAccountRequest dto, @MappingTarget User user);

}
