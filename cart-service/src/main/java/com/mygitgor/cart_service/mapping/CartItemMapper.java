package com.mygitgor.cart_service.mapping;

import com.mygitgor.cart_service.domain.CartItem;
import com.mygitgor.cart_service.dto.CartItemDto;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {CartMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CartItemMapper {

    @Mapping(source = "id", target = "id")
    CartItemDto toCartItemDto(CartItem cartItem);

    @InheritInverseConfiguration
    CartItem toCartItem(CartItemDto dto);
}
