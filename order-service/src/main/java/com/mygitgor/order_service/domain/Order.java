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

    private String userId;
    private UUID sellerId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    private UUID addressId;

    private UUID paymentId;

    private Integer totalMrpPrice;
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

    private String shippingMethod;
    private String trackingNumber;
    private String customerNotes;

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
                .mapToInt(OrderItem::getSellingPrice)
                .sum();
    }

    public Integer calculateTotalMrpPrice() {
        return orderItems.stream()
                .mapToInt(OrderItem::getMrpPrice)
                .sum();
    }

    public Integer calculateDiscount() {
        return calculateTotalMrpPrice() - calculateTotalPrice();
    }

    public static Order create(String userId, UUID addressId, UUID sellerId) {
        Order order = new Order();
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setSellerId(sellerId);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setTotalMrpPrice(0);
        order.setTotalSellingPrice(0);
        order.setDiscount(0);
        order.setTotalItem(0);
        return order;
    }

    public void updateTotals() {
        this.totalMrpPrice = calculateTotalMrpPrice();
        this.totalSellingPrice = calculateTotalPrice();
        this.discount = calculateDiscount();
        this.totalItem = orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

}
