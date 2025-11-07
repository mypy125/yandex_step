package com.mygitgor.user_service.mapper;

import com.mygitgor.user_service.domain.User;
import com.mygitgor.user_service.dto.UserDto;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {AddressMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "mobile", target = "mobile")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "addresses", target = "addresses")
    @Mapping(source = "usedCoupons", target = "usedCoupons")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    UserDto toUserDto(User user);

    @InheritInverseConfiguration
    @Mapping(target = "password", ignore = true)
    User toUser(UserDto userDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", ignore = true)
    void updateUserFromDto(UserDto userDto, @MappingTarget User user);

}
