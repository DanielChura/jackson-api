package com.jackson_api.JacksonApi.presentation.controller;

import com.jackson_api.JacksonApi.application.dto.request.CreateRoleRequest;
import com.jackson_api.JacksonApi.application.dto.response.RoleResponse;
import com.jackson_api.JacksonApi.application.service.RoleService;
import com.jackson_api.JacksonApi.presentation.response.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping()
    public ResponseEntity<PagedResponse<RoleResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(PagedResponse.from(roleService.getAllRoles(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody CreateRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createRole(request));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponse> patch(@PathVariable UUID id, @Valid @RequestBody CreateRoleRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(roleService.patchRole(id, request));
    }
}
