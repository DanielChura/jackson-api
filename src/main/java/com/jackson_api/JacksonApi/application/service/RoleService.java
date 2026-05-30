package com.jackson_api.JacksonApi.application.service;

import com.jackson_api.JacksonApi.application.dto.request.CreateRoleRequest;
import com.jackson_api.JacksonApi.application.dto.response.RoleResponse;
import com.jackson_api.JacksonApi.application.mapper.RoleMapper;
import com.jackson_api.JacksonApi.domain.entity.Role;
import com.jackson_api.JacksonApi.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;

    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream().map(roleMapper::toResponse).toList();
    }

    public RoleResponse getRoleById(UUID id) {
        return roleMapper.toResponse(roleRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Role no encontrado")));
    }

    public RoleResponse createRole(CreateRoleRequest request) {
        Role role = roleMapper.toCreate(request);
        return roleMapper.toResponse(roleRepository.save(role));
    }

    public RoleResponse patchRole(UUID id, CreateRoleRequest request) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role no encontrado"));
        return roleMapper.toResponse(roleRepository.save(roleMapper.toPatch(role, request)));
    }
}
