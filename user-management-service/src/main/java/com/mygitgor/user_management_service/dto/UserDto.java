package com.mygitgor.user_management_service.dto;

import java.util.List;
import java.util.UUID;

public record UserDto (UUID id, String email, String fullName, List<String> role){
}
