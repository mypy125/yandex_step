package com.mygitgor.dto;

import com.mygitgor.user.domain.Role;

public record UserDto (String id, String email, String fullName, Role role){
}
