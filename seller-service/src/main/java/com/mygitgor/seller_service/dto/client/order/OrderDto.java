package com.mygitgor.seller_service.dto.client.order;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private UUID id;
    private String orderId;

    private UUID userId;
    private UUID sellerId;
    private UUID addressId;

    private List<OrderItemDto> orderItems;

    private PaymentDetailsDto paymentDetails;

    private double totalMrpPrice;
    private Integer totalSellingPrice;
    private Integer discount;
    private Integer totalItem;

    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;

    private LocalDateTime orderDate;
    private LocalDateTime deliverDate;

}
