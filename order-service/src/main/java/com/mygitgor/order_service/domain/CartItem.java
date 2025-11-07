package com.mygitgor.order_service.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cart_items")
@ToString(callSuper = true, exclude = "cart")
@EqualsAndHashCode(callSuper = true,exclude = "cart")
public class CartItem extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    private UUID productId;

    private String size;
    private int quantity = 1;

    private Integer mrpPrice;
    private Integer sellingPrice;

    private UUID userId;

    public static CartItem create(UUID productId, String size, int quantity,
                                  Integer mrpPrice, Integer sellingPrice, UUID userId) {
        CartItem item = new CartItem();
        item.setProductId(productId);
        item.setSize(size);
        item.setQuantity(quantity);
        item.setMrpPrice(mrpPrice);
        item.setSellingPrice(sellingPrice);
        item.setUserId(userId);
        return item;
    }

    public Integer getTotalPrice() {
        return sellingPrice * quantity;
    }

    public Integer getTotalMrpPrice() {
        return mrpPrice * quantity;
    }
}
