package com.mygitgor.product_service.domain;

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
    private HomeCategory category;
}