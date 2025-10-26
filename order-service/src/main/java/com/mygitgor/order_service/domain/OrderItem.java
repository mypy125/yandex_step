package com.mygitgor.order_service.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_items")
@ToString(callSuper = true, exclude = "order")
@EqualsAndHashCode(callSuper = true, exclude = "order")
public class OrderItem extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order order;

    private UUID productId;

    private String size;
    private int quantity;
    private Integer mrpPrice;
    private Integer sellingPrice;



    public Integer getTotalPrice() {
        return sellingPrice != null ? sellingPrice * quantity : 0;
    }

    public Integer getTotalMrpPrice() {
        return mrpPrice != null ? mrpPrice * quantity : 0;
    }
}
