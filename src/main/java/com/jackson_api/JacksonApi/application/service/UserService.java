package com.jackson_api.JacksonApi.application.service;

import com.jackson_api.JacksonApi.application.dto.request.CreateUserRequest;
import com.jackson_api.JacksonApi.application.dto.response.UserResponse;
import com.jackson_api.JacksonApi.application.mapper.UserMapper;
import com.jackson_api.JacksonApi.domain.entity.Role;
import com.jackson_api.JacksonApi.domain.entity.User;
import com.jackson_api.JacksonApi.domain.repository.RoleRepository;
import com.jackson_api.JacksonApi.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> responses = new ArrayList<>();
        for (User user : users) {
            responses.add(userMapper.toResponse(user));
        }
        ;
        return responses;
    }

    public UserResponse getUserById(UUID id) {
        return userMapper.toResponse(
                userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado")));
    }

    public UserResponse createUser(@NonNull CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya existe");
        }

        Role role = roleRepository.findByName("client")
                .orElseThrow(() -> new RuntimeException("Role no encontrado"));

        User user = new User();
        user.setRole(role);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setPhone(request.getPhone());

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashedPassword);

        user.setIsActive(true);

        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse updateUser(UUID id, CreateUserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setAddress(request.getAddress());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setLastName(request.getLastName());

        return userMapper.toResponse(userRepository.save(user));
    }

    public void delete(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        userRepository.delete(user);
    }
}
