package com.mygitgor.user_service.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Wishlist extends BaseEntity {

    @OneToOne
    private User user;

    @ElementCollection
    @CollectionTable(
            name = "wishlist_products",
            joinColumns = @JoinColumn(name = "wishlist_id")
    )
    @Column(name = "product_id")
    private Set<UUID> products = new HashSet<>();
}
