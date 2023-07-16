package com.sheelu.spring.auth.controllers;

import com.sheelu.spring.auth.service.UserService;
import com.sheelu.spring.auth.util.UserContextUtils;
import com.sheelu.spring.auth.controllers.dtos.request.UserSignupRequest;
import com.sheelu.spring.auth.controllers.dtos.request.UserUpdateRequestDTO;
import com.sheelu.spring.auth.controllers.dtos.response.UserProfile;
import com.sheelu.spring.auth.exceptions.BadRequest;
import com.sheelu.spring.auth.exceptions.EntityAlreadyExistException;
import com.sheelu.spring.auth.exceptions.EntityNotFoundException;
import com.sheelu.spring.auth.exceptions.UnAuthorizedAccessException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserContextUtils userContextUtils;

    @Autowired
    public UserController(UserService userService, UserContextUtils userContextUtils) {
        this.userService = userService;
        this.userContextUtils = userContextUtils;
    }

    @PostMapping("/api/v1/users")
    public ResponseEntity<String> createUser(@RequestBody @Valid UserSignupRequest userSignupRequest) throws EntityAlreadyExistException {
        logger.info("Got Request to create an user: {}", userSignupRequest);
        userService.createStandardUser(userSignupRequest);
        logger.info("User has been created successfully for request: {}", userSignupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("User has been created successfully");
    }

    @GetMapping("/api/v1/users")
    public ResponseEntity<List<UserProfile>> getAllUsers() {
        logger.info("Got request to get all users");
        if (userContextUtils.isAdminUser()) {
            List<UserProfile> allUsers = userService.getAllUsers();
            return ResponseEntity.ok(allUsers);
        }
        throw new UnAuthorizedAccessException("User is not allowed to access this resource");
    }

    @GetMapping("/api/v1/users/{userName}")
    public ResponseEntity<UserProfile> getUser(@PathVariable(value = "userName") String userName) throws EntityNotFoundException {
        logger.info("Got to get user profile for for userId: {}", userName);
        if (StringUtils.isBlank(userName)) {
            throw new BadRequest("userName cannot be blank");
        }
        if (!this.isAuthorizedUser(userName)) {
            throw new UnAuthorizedAccessException("User is not allowed to access this resource");
        }
        UserProfile userProfile = userService.getUser(userName);
        return ResponseEntity.ok(userProfile);
    }

    @DeleteMapping("/api/v1/users/{userName}")
    public ResponseEntity<String> deleteUser(@PathVariable(value = "userName") String userName) throws EntityNotFoundException {
        logger.info("Got delete request for userName: {}", userName);
        if (StringUtils.isBlank(userName)) {
            throw new BadRequest("UserId cannot be blank");
        }
        if (!this.isAuthorizedUser(userName)) {
            throw new UnAuthorizedAccessException("User is not allowed to access this resource");
        }
        userService.deleteUser(userName);
        logger.info("User have deleted successfully, userName: {}", userName);
        return ResponseEntity.ok("User has been deleted successfully");
    }

    @PatchMapping("/api/v1/users/{userName}")
    public ResponseEntity<String> updateUser(@PathVariable(value = "userName") String userName, @RequestBody UserUpdateRequestDTO userUpdateRequestDTO) throws EntityNotFoundException {
        logger.info("Got Update request for userName: {}", userName);
        if (StringUtils.isBlank(userName)) {
            throw new BadRequest("UserId cannot be blank");
        }
        if (!this.isAuthorizedUser(userName)) {
            throw new UnAuthorizedAccessException("User is not allowed to access this resource");
        }
        userService.updateUser(userName, userUpdateRequestDTO);
        logger.info("User has been updated successfully, userName: {}", userName);
        return ResponseEntity.ok("User has been updated successfully");
    }

    private boolean isAuthorizedUser(String userName) {
        if (userContextUtils.isAdminUser()) return true;
        return userContextUtils.loggedInUserName().equals(userName);
    }
}
