package com.mygitgor.product_catalog_service.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Category extends BaseEntity {
    private String name;

    @Column(unique = true)
    @NotNull
    private String categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category parentCategory;

    @NotNull
    private Integer level;
}
