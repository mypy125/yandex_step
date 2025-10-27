package com.mygitgor.user_management_service.controller;

import com.mygitgor.user_management_service.dto.SignupRequest;
import com.mygitgor.user_management_service.dto.UserDto;
import com.mygitgor.user_management_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@RequestParam String email){
         UserDto user = userService.findByEmail(email);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody SignupRequest request) {
        UserDto user = userService.createUser(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}
