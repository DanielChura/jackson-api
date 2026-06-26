package com.jackson_api.JacksonApi.application.service;

import com.jackson_api.JacksonApi.application.dto.request.LoginRequest;
import com.jackson_api.JacksonApi.application.dto.request.RegisterRequest;
import com.jackson_api.JacksonApi.application.dto.response.AuthResponse;
import com.jackson_api.JacksonApi.domain.entity.Cart;
import com.jackson_api.JacksonApi.domain.entity.Role;
import com.jackson_api.JacksonApi.domain.entity.User;
import com.jackson_api.JacksonApi.domain.exceptions.ResourceNotFoundException;
import com.jackson_api.JacksonApi.domain.repository.CartRepository;
import com.jackson_api.JacksonApi.domain.repository.RoleRepository;
import com.jackson_api.JacksonApi.domain.repository.UserRepository;
import com.jackson_api.JacksonApi.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CartRepository cartRepository;

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getRole().getName());
        return new AuthResponse(token, user.getEmail(), user.getRole().getName());
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Role role = roleRepository.findByName("client")
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        User user = new User();

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(role);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAddress(request.getAddress());
        user.setPhone(request.getPhone());
        user.setIsActive(true);

        userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);

        String token = jwtUtil.generateToken(user.getId(), role.getName());
        return new AuthResponse(token, user.getEmail(), user.getRole().getName());
    }
}
