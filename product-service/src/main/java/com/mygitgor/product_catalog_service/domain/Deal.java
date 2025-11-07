package com.mygitgor.product_catalog_service.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true, exclude = "category")
@EqualsAndHashCode(callSuper = true, exclude = "category")
public class Deal extends BaseEntity{
    private Integer discount;

    @OneToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private HomeCategory category;
}