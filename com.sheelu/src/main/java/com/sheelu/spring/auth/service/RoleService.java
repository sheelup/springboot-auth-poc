package com.sheelu.spring.auth.service;

import com.sheelu.spring.auth.controllers.dtos.request.RoleDTO;
import com.sheelu.spring.auth.exceptions.EntityAlreadyExistException;

public interface RoleService {
    void createNewRole(RoleDTO roleDTO) throws EntityAlreadyExistException;
}
