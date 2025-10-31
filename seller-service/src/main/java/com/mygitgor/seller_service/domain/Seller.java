package com.mygitgor.seller_service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mygitgor.seller_service.domain.details.BankDetails;
import com.mygitgor.seller_service.domain.details.BusinessDetails;
import com.mygitgor.seller_service.dto.USER_ROLE;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sellers")
public class Seller extends BaseEntity{
    @Column(name = "seller_name")
    private String sellerName;

    @Column(unique = true, nullable = false)
    private String email;
    private String mobile;

    @NotBlank(message = "Password is required")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Enumerated(EnumType.STRING)
    private USER_ROLE role;

    @Embedded
    private BusinessDetails businessDetails = new BusinessDetails();

    @Embedded
    private BankDetails bankDetails = new BankDetails();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_address_id")
    @ToString.Exclude
    private Address pickupAddress = new Address();

    private String NDS;

    @Column(name = "email_verified")
    private boolean isEmailVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status")
    private AccountStatus accountStatus = AccountStatus.PENDING_VERIFICATION;

    @PrePersist
    public void prePersist() {
        if(this.getRole()==null){
            this.setRole(USER_ROLE.ROLE_SELLER);
        }

    }
}
