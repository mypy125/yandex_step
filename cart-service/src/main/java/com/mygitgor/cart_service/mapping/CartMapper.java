package com.mygitgor.cart_service.mapping;

import com.mygitgor.cart_service.domain.Cart;
import com.mygitgor.cart_service.dto.CartDto;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {CartItemMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CartMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "cartItems", target = "cartItems")
    @Mapping(source = "totalSellingPrice", target = "totalSellingPrice")
    @Mapping(source = "totalItem", target = "totalItem")
    @Mapping(source = "totalMrpPrice", target = "totalMrpPrice")
    @Mapping(source = "discount", target = "discount")
    @Mapping(source = "couponCode", target = "couponCode")
    CartDto toCartDto(Cart cart);

    @InheritInverseConfiguration
    Cart toCart(CartDto dto);
}
