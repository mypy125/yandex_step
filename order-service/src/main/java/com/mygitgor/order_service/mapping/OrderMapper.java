package com.mygitgor.order_service.mapping;

import com.mygitgor.order_service.domain.Order;
import com.mygitgor.order_service.dto.OrderDto;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(
        componentModel = "spring",
        uses = {OrderItemMapper.class, PaymentDetailsMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderMapper {

    OrderDto toOrderDto(Order order);
    Order toOrder(OrderDto orderDto);

    List<OrderDto> toOrderDtoList(List<Order> orders);
    List<Order> toOrderList(List<OrderDto> orderDto);
    Set<OrderDto> toOrderDtoSet(Set<Order> orders);
    Set<Order> toOrderSet(Set<OrderDto> orderDto);
}