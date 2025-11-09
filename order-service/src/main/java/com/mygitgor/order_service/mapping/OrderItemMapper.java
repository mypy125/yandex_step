package com.mygitgor.order_service.mapping;

import com.mygitgor.order_service.domain.OrderItem;
import com.mygitgor.order_service.dto.OrderItemDto;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderItemMapper {

    @Mapping(target = "order", ignore = true)
    OrderItemDto toOrderItemDto(OrderItem orderItem);

    @Mapping(target = "order", ignore = true)
    OrderItem toOrderItem(OrderItemDto dto);

    List<OrderItemDto> toOrderItemDtoList(List<OrderItem> items);
    List<OrderItem> toOrderItemList(List<OrderItemDto> dto);
    Set<OrderItemDto> toOrderItemDtoSet(Set<OrderItem> items);
    Set<OrderItem> toOrderItemSet(Set<OrderItemDto> dto);
}