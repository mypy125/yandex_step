package com.mygitgor.seller_service.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Transaction extends BaseEntity {

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private UUID orderId;

    @ManyToOne
    private Seller seller;

    private LocalDateTime date = LocalDateTime.now();
}
