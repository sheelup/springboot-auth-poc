package com.sheelu.spring.auth.service.impl;

import com.sheelu.spring.auth.controllers.dtos.request.RoleDTO;
import com.sheelu.spring.auth.dao.RoleRepository;
import com.sheelu.spring.auth.exceptions.EntityAlreadyExistException;
import com.sheelu.spring.auth.models.Role;
import com.sheelu.spring.auth.models.UserRole;
import com.sheelu.spring.auth.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(@Autowired RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void createNewRole(RoleDTO roleDTO) throws EntityAlreadyExistException {
        validateDuplicateRole(roleDTO.getRoleName());
        Role role = new Role();
        role.setName(roleDTO.getRoleName());
        roleRepository.save(role);
    }

    private void validateDuplicateRole(UserRole roleName) throws EntityAlreadyExistException {
        if (roleRepository.findByName(roleName).isPresent()) {
            throw new EntityAlreadyExistException("Role already exist with given name");
        }
    }
}
