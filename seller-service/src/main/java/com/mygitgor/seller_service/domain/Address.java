package com.mygitgor.seller_service.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Address extends BaseEntity{
    private String name;
    private String locality;
    private String address;
    private String city;
    private String state;
    private String pinCode;
    private String mobile;

    @OneToOne(mappedBy = "pickupAddress", fetch = FetchType.LAZY)
    private Seller seller;
}
