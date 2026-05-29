package com.jackson_api.JacksonApi.presentation.controller;

import com.jackson_api.JacksonApi.application.dto.request.CreateRoleRequest;
import com.jackson_api.JacksonApi.application.dto.response.RoleResponse;
import com.jackson_api.JacksonApi.application.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("role")
public class RoleController {
    private RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping()
    public ResponseEntity<List<RoleResponse>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PostMapping()
    public ResponseEntity<RoleResponse> create(@RequestBody CreateRoleRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(roleService.createRole(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RoleResponse> patch(@PathVariable UUID id, @RequestBody CreateRoleRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(roleService.patchRole(id, request));
    }
}
