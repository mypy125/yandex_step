package com.mygitgor.order_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
@ToString(callSuper = true, exclude = "orderItems")
@EqualsAndHashCode(callSuper = true,exclude = "orderItems")
public class Order extends BaseEntity {
    private String orderId;

    private UUID userId;
    private UUID sellerId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    private UUID addressId;

    @Embedded
    private PaymentDetails paymentDetails = new PaymentDetails();

    private double totalMrpPrice;
    private Integer totalSellingPrice;
    private Integer discount;
    private Integer totalItem;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @CreationTimestamp
    private LocalDateTime orderDate;

    private LocalDateTime deliverDate;

    @PrePersist
    public void prePersist() {
        if (this.orderDate == null) {
            this.orderDate = LocalDateTime.now();
        }
        if (this.deliverDate == null) {
            this.deliverDate = this.orderDate.plusDays(7);
        }
        if (this.orderId == null) {
            this.orderId = "ORD_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }

    public Integer calculateTotalPrice() {
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }

    public Integer calculateTotalMrpPrice() {
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalMrpPrice)
                .sum();
    }

    public Integer calculateDiscount() {
        return calculateTotalMrpPrice() - calculateTotalPrice();
    }

    public static Order create(UUID userId, UUID addressId) {
        Order order = new Order();
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        return order;
    }

}
