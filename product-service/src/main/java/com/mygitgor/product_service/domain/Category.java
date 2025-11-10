package com.mygitgor.product_service.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true, exclude = "parentCategory")
@EqualsAndHashCode(callSuper = true, exclude = "parentCategory")
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
