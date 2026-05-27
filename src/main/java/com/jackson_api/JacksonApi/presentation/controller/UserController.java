package com.jackson_api.JacksonApi.presentation.controller;

import com.jackson_api.JacksonApi.application.dto.request.CreateUserRequest;
import com.jackson_api.JacksonApi.application.dto.response.UserResponse;
import com.jackson_api.JacksonApi.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping()
    public List<UserResponse> getAll() {
        return userService.getAllUsers();
    }

    @PostMapping()
    public UserResponse createUser(@RequestBody CreateUserRequest user) {
        return userService.createUser(user);
    }
}
