package com.mygitgor.product_catalog_service.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Deal extends BaseEntity{
    private Integer discount;

    @OneToOne
    private HomeCategory category;
}