package com.project.medtech.controller;

import com.project.medtech.dto.MyEntry;
import com.project.medtech.dto.RolePermissionDto;
import com.project.medtech.dto.PermissionDto;
import com.project.medtech.dto.RoleDto;
import com.project.medtech.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/access")
@Api( "REST APIs related to `Role` and `Permission` Entities")
public class RoleController {

    private final RoleService roleService;


    @ApiOperation(value = "получение ролей и их разрешений")
    @GetMapping("/get-roles-permissions")
    public ResponseEntity<HashMap<String, Map<String, String>>> getRolesPermissions() {
        return ResponseEntity.ok(roleService.getRolesPermissions());
    }

    @ApiOperation(value = "создание новой роли")
    @PostMapping("/create-role")
    public ResponseEntity<RoleDto> createRole(@RequestBody RoleDto roleDto) {
        return ResponseEntity.ok(roleService.createRole(roleDto));
    }

    @ApiOperation(value = "создание нового разрешения")
    @PostMapping("/create-permission")
    public ResponseEntity<PermissionDto> createPermission(@RequestBody PermissionDto permissionDto) {
        return ResponseEntity.ok(roleService.createPermission(permissionDto));
    }

    @ApiOperation(value = "добавление определенного разрешения к определенной роли")
    @PostMapping("/add-permission-to-role")
    public ResponseEntity<RolePermissionDto> addPermissionToRole(@RequestBody RolePermissionDto rolePermissionDto) {
        return ResponseEntity.ok(roleService.addPermissionToRole(rolePermissionDto));
    }

    @ApiOperation(value = "удаление определенного разрешения из определенной роли")
    @PutMapping("/delete-permission-from-role")
    public ResponseEntity<MyEntry<String, Object>> deletePermissionFromRole(@RequestBody RolePermissionDto rolePermissionDto) {
        return ResponseEntity.ok(roleService.deletePermissionFromRole(rolePermissionDto));
    }
}
