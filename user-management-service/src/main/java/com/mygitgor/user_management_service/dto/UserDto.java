package com.mygitgor.user_management_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mygitgor.user_management_service.domain.USER_ROLE;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private UUID id;
    private String fullName;
    private String email;
    private String mobile;
    private USER_ROLE role;
    private Set<AddressDto> addresses;
    private Set<UUID> usedCoupons;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
