package com.jackson_api.JacksonApi.application.mapper;

import com.jackson_api.JacksonApi.application.dto.request.CreateRoleRequest;
import com.jackson_api.JacksonApi.application.dto.response.RoleResponse;
import com.jackson_api.JacksonApi.domain.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public RoleResponse toResponse(Role response) {
        RoleResponse role = new RoleResponse();

        role.setDescription(response.getDescription());
        role.setName(response.getName());
        role.setId(response.getId());
        role.setCreatedAt(response.getCreatedAt());
        return role;
    }

    public Role toCreate(CreateRoleRequest request) {
        Role role = new Role();

        role.setDescription(request.getDescription());
        role.setName(request.getName());
        return role;
    }

    public Role toPatch(Role role, CreateRoleRequest request) {
        if (!(request.getName() == null)) {
            role.setName(request.getName());
        }
        if (!(request.getDescription() == null)) {
            role.setDescription(request.getDescription());
        }
        return role;
    }
}
