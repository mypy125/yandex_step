package com.mygitgor.user_service.mapper;

import com.mygitgor.user_service.domain.Address;
import com.mygitgor.user_service.dto.AddressDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressMapper {

    @Mapping(source = "user.id", target = "userId")
    AddressDto toAddressDto(Address address);

    @Mapping(target = "user", ignore = true)
    Address toAddress(AddressDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    void updateAddressFromDto(AddressDto dto, @MappingTarget Address entity);
}
