package com.project.medtech.service;

import com.project.medtech.dto.MyEntry;
import com.project.medtech.dto.RolePermissionDto;
import com.project.medtech.dto.PermissionDto;
import com.project.medtech.dto.RoleDto;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.model.PermissionEntity;
import com.project.medtech.model.RoleEntity;
import com.project.medtech.repository.PermissionRepository;
import com.project.medtech.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    private final PermissionRepository permissionRepository;


    public HashMap<String, Map<String, String>> getRolesPermissions() {
        HashMap<String, Map<String, String>> map = new HashMap<>();

        roleRepository.findAll()
                .forEach(
                        r -> map.put(
                                r.getName(),
                                r.getPermissionEntities()
                                        .stream()
                                        .collect(
                                                Collectors.toMap
                                                        (PermissionEntity::getName, PermissionEntity::getDescription)
                                        )
                        )
                );

        return map;
    }

    public RoleDto createRole(RoleDto roleDto) {
        RoleEntity roleEntity = new RoleEntity();

        roleEntity.setName(roleDto.getName());

        roleRepository.save(roleEntity);

        return roleDto;
    }

    public PermissionDto createPermission(PermissionDto permissionDto) {
        PermissionEntity permissionEntity = new PermissionEntity();

        permissionEntity.setName(permissionDto.getName());
        permissionEntity.setDescription(permissionDto.getDescription());

        permissionRepository.save(permissionEntity);

        return permissionDto;
    }

    public RolePermissionDto addPermissionToRole(RolePermissionDto rolePermissionDto) {
        RoleEntity roleEntity = roleRepository.findByName(rolePermissionDto.getRole())
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException("No role was found with name: " +
                                        rolePermissionDto.getRole())
                );

        PermissionEntity permissionEntity = permissionRepository.findByName(rolePermissionDto.getPermission())
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException("No permission was found with name: " +
                                        rolePermissionDto.getPermission())
                );

        Set<PermissionEntity> permissionEntitySet = roleEntity.getPermissionEntities();

        permissionEntitySet.add(permissionEntity);

        roleRepository.save(roleEntity);

        return rolePermissionDto;
    }

    public MyEntry<String, Object> deletePermissionFromRole(RolePermissionDto rolePermissionDto) {
        RoleEntity roleEntity = roleRepository.findByName(rolePermissionDto.getRole())
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException("No role was found with name: " +
                                        rolePermissionDto.getRole())
                );

        PermissionEntity permissionEntity = permissionRepository.findByName(rolePermissionDto.getPermission())
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException("No permission was found with name: " +
                                        rolePermissionDto.getPermission())
                );

        Set<PermissionEntity> permissions = roleEntity.getPermissionEntities();

        permissions.remove(permissionEntity);

        roleRepository.save(roleEntity);

        return new MyEntry<>(
                roleEntity.getName(),
                roleEntity.getPermissionEntities()
                        .stream()
                        .collect(Collectors.toMap
                                (PermissionEntity::getName, PermissionEntity::getDescription)
                        )
        );
    }

}
