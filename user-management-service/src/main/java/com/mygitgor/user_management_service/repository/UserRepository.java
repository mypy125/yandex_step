package com.mygitgor.user_management_service.repository;

import com.mygitgor.user_management_service.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);
}
