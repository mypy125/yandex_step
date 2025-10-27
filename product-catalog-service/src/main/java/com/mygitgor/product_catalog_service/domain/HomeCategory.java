package com.mygitgor.product_catalog_service.domain;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true, exclude = "section")
@EqualsAndHashCode(callSuper = true, exclude = "section")
public class HomeCategory extends BaseEntity {
    private String name;
    private String image;
    private String categoryId;
    private HomeCategorySection section;
}
