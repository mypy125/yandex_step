package com.mygitgor.user_management_service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor
@ToString(callSuper = true, exclude = {"addresses", "usedCoupons", "password"})
@EqualsAndHashCode(callSuper = true, exclude = {"addresses", "usedCoupons"})
public class User extends BaseEntity{
    @ToString.Include
    private String fullName;

    @Column(unique = true, nullable = false)
    @ToString.Include
    private String email;

    private String mobile;

    @NotBlank(message = "Password is required")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private USER_ROLE role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Address> addresses = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_used_coupons", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "coupon_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<UUID> usedCoupons = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if(this.getRole()==null){
            this.setRole(USER_ROLE.ROLE_CUSTOMER);
        }
    }
}
