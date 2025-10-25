package com.mygitgor.order_service.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cart_items")
public class CartItem extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    private UUID productId;

    private String size;
    private int quantity = 1;

    private Integer mrpPrice;
    private Integer sellingPrice;

    private UUID userId;
}
