package com.jackson_api.JacksonApi.presentation.controller;

import com.jackson_api.JacksonApi.application.dto.request.CreateUserRequest;
import com.jackson_api.JacksonApi.application.dto.response.UserResponse;
import com.jackson_api.JacksonApi.application.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<List<UserResponse>> getAll() {
        List<UserResponse> response = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping()
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest user) {
        UserResponse response = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable UUID id, @Valid @RequestBody CreateUserRequest user) {
        UserResponse response = userService.updateUser(id, user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
