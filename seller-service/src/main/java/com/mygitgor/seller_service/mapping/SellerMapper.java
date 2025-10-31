package com.mygitgor.seller_service.mapping;


import com.mygitgor.seller_service.domain.Seller;
import com.mygitgor.seller_service.dto.SellerAuthInfo;
import com.mygitgor.seller_service.dto.SellerDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SellerMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "sellerName", target = "sellerName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "mobile", target = "mobile")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "businessDetails", target = "businessDetails")
    @Mapping(source = "bankDetails", target = "bankDetails")
    @Mapping(source = "pickupAddress", target = "pickupAddress")
    @Mapping(source = "NDS", target = "NDS")
    @Mapping(source = "emailVerified", target = "emailVerified")
    @Mapping(source = "accountStatus", target = "accountStatus")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    SellerDto toSellerDto(Seller seller);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "sellerName", target = "sellerName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "mobile", target = "mobile")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "businessDetails", target = "businessDetails")
    @Mapping(source = "bankDetails", target = "bankDetails")
    @Mapping(source = "pickupAddress", target = "pickupAddress")
    @Mapping(source = "NDS", target = "NDS")
    @Mapping(source = "emailVerified", target = "emailVerified")
    @Mapping(source = "accountStatus", target = "accountStatus")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    Seller toSeller(SellerDto sellerDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(source = "sellerName", target = "sellerName")
    @Mapping(source = "mobile", target = "mobile")
    @Mapping(source = "password", target = "password", conditionExpression = "java(sellerDto.getPassword() != null && !sellerDto.getPassword().isEmpty())")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "businessDetails", target = "businessDetails")
    @Mapping(source = "bankDetails", target = "bankDetails")
    @Mapping(source = "pickupAddress", target = "pickupAddress")
    @Mapping(source = "NDS", target = "NDS")
    @Mapping(source = "emailVerified", target = "emailVerified")
    @Mapping(source = "accountStatus", target = "accountStatus")
    void updateSellerFromDto(SellerDto sellerDto, @MappingTarget Seller seller);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "sellerName", target = "fullName")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "emailVerified", target = "emailVerified")
    SellerAuthInfo toSellerAuthInfo(Seller seller);
}
