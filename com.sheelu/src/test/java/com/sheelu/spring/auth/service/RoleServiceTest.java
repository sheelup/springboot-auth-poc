package com.sheelu.spring.auth.service;

import com.sheelu.spring.auth.models.Role;
import com.sheelu.spring.auth.models.UserRole;
import com.sheelu.spring.auth.controllers.dtos.request.RoleDTO;
import com.sheelu.spring.auth.dao.RoleRepository;
import com.sheelu.spring.auth.exceptions.EntityAlreadyExistException;
import com.sheelu.spring.auth.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

public class RoleServiceTest {

    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    RoleRepository roleRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void TestCreateNewRole() throws EntityAlreadyExistException {
        //Case1: create duplicate role should fail
        Mockito.when(roleRepository.findByName(UserRole.STANDARD)).thenReturn(Optional.of(new Role()));
        Assertions.assertThrows(EntityAlreadyExistException.class, () -> {
            roleService.createNewRole(new RoleDTO(UserRole.STANDARD));
        });

        //Case2: create new role successfully
        Mockito.when(roleRepository.findByName(UserRole.ADMIN)).thenReturn(Optional.empty());
        Mockito.when(roleRepository.save(Mockito.any())).thenReturn(new Role());
        roleService.createNewRole(new RoleDTO(UserRole.ADMIN));
    }


}
