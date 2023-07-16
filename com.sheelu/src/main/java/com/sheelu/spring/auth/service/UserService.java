package com.sheelu.spring.auth.service;

import com.sheelu.spring.auth.controllers.dtos.request.UserSignupRequest;
import com.sheelu.spring.auth.controllers.dtos.request.UserUpdateRequestDTO;
import com.sheelu.spring.auth.exceptions.EntityAlreadyExistException;
import com.sheelu.spring.auth.controllers.dtos.response.UserProfile;
import com.sheelu.spring.auth.exceptions.EntityNotFoundException;

import java.util.List;


public interface UserService {
    void createStandardUser(UserSignupRequest userSignupRequest) throws EntityAlreadyExistException;
    void createAdminUser(UserSignupRequest userSignupRequest) throws EntityAlreadyExistException;
    UserProfile getUser(String userId) throws EntityNotFoundException;
    void updateUser(String userId, UserUpdateRequestDTO userUpdateRequestDTO) throws EntityNotFoundException;
    void deleteUser(String userId) throws EntityNotFoundException;
    List<UserProfile> getAllUsers();
}
