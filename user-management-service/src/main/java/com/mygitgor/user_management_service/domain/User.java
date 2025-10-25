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
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class User extends BaseEntity{
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;
    private String mobile;

    @NotBlank(message = "Password is required")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private USER_ROLE role;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Address> addresses = new HashSet<>();

    private Set<UUID> usedCoupons = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if(this.getRole()==null){
            this.setRole(USER_ROLE.ROLE_CUSTOMER);
        }
    }
}
