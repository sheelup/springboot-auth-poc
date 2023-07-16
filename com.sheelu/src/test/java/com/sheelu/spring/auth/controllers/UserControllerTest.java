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
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

public class UserControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UserContextUtils userContextUtils;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @SneakyThrows
    @Test
    public void testCreateUser() {
        //Case: User created successfully
        UserSignupRequest signupRequest = UserSignupRequest.builder()
                .userName("u1")
                .firstName("d")
                .lastName("p")
                .build();
        Mockito.doNothing().when(userService).createStandardUser(signupRequest);
        Assertions.assertDoesNotThrow(() -> {
            ResponseEntity<String> response = userController.createUser(signupRequest);
            Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
            Assertions.assertEquals("User has been created successfully",response.getBody());
        });

        //Case: Create duplicate user
        EntityAlreadyExistException ex = new EntityAlreadyExistException("");
        Mockito.doThrow(ex).when(userService).createStandardUser(signupRequest);
        Assertions.assertThrows(EntityAlreadyExistException.class, () -> {
            userController.createUser(signupRequest);
        });
    }

    @Test
    public void testGetAllUsers() {
        //Case: Non-Admin user should not be allowed
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(false);
        Assertions.assertThrows(UnAuthorizedAccessException.class, () -> {
            userController.getAllUsers();
        });

        //Case: Get users successfully
        List<UserProfile> users = Arrays.asList(
                UserProfile.builder().userName("u1").firstName("f1").lastName("l1").build(),
                UserProfile.builder().userName("u2").firstName("f2").lastName("l2").build(),
                UserProfile.builder().userName("u3").firstName("f3").lastName("l3").build()
        );
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(true);
        Mockito.when(userService.getAllUsers()).thenReturn(users);
        Assertions.assertDoesNotThrow(() -> {
            ResponseEntity<List<UserProfile>> response = userController.getAllUsers();
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals(users, response.getBody());
        });
    }

    @SneakyThrows
    @Test
    public void testGetUser() {
        //Case: Blank username
        Assertions.assertThrows(BadRequest.class, () -> {
            userController.getUser("");
        });

        //Case: Unauthorized user access
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(false);
        Mockito.when(userContextUtils.loggedInUserName()).thenReturn("u2");
        Assertions.assertThrows(UnAuthorizedAccessException.class, () -> {
            userController.getUser("u1");
        });

        //Case: Get user successfully
        UserProfile userProfile = UserProfile.builder().userName("u1").firstName("f1").lastName("l1").build();
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(true);
        Mockito.when(userContextUtils.loggedInUserName()).thenReturn("u1");
        Mockito.when(userService.getUser("u1")).thenReturn(userProfile);
        Assertions.assertDoesNotThrow(() -> {
            ResponseEntity<UserProfile> response = userController.getUser("u1");
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals(userProfile, response.getBody());
        });

        //Case: User does not exist with given userName
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(true);
        Mockito.when(userContextUtils.loggedInUserName()).thenReturn("u1");
        Mockito.when(userService.getUser("u1")).thenThrow(new EntityNotFoundException("Not found"));
        Assertions.assertThrows(EntityNotFoundException.class ,() -> {
            userController.getUser("u1");
        });
    }

    @SneakyThrows
    @Test
    public void testDeleteUser() {
        //Case: Blank username
        Assertions.assertThrows(BadRequest.class, () -> {
            userController.deleteUser("");
        });

        //Case: Unauthorized user to for deletion
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(false);
        Mockito.when(userContextUtils.loggedInUserName()).thenReturn("u2");
        Assertions.assertThrows(UnAuthorizedAccessException.class, () -> {
            userController.deleteUser("u1");
        });

        //Case: Delete user successfully
        UserProfile userProfile = UserProfile.builder().userName("u1").firstName("f1").lastName("l1").build();
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(true);
        Mockito.when(userContextUtils.loggedInUserName()).thenReturn("u1");
        Mockito.doNothing().when(userService).deleteUser("u1");
        Assertions.assertDoesNotThrow(() -> {
            ResponseEntity<String> response = userController.deleteUser("u1");
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals("User has been deleted successfully", response.getBody());
        });

        //Case: Delete User which does not exist
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(true);
        Mockito.when(userContextUtils.loggedInUserName()).thenReturn("u1");
        Mockito.doThrow(new EntityNotFoundException("Not found")).when(userService).deleteUser("u1");
        Assertions.assertThrows(EntityNotFoundException.class ,() -> {
            userController.deleteUser("u1");
        });
    }

    @SneakyThrows
    @Test
    public void testUpdateUser(){
        UserUpdateRequestDTO updateRequest =
                UserUpdateRequestDTO.builder().firstName("Sheela").lastName("Purbiya").build();
        //Case: Blank username
        Assertions.assertThrows(BadRequest.class, () -> {
            userController.updateUser("", updateRequest);
        });

        //Case: Unauthorized user for update operation
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(false);
        Mockito.when(userContextUtils.loggedInUserName()).thenReturn("u2");
        Assertions.assertThrows(UnAuthorizedAccessException.class, () -> {
            userController.updateUser("u1", updateRequest);
        });

        //Case: Update user successfully
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(true);
        Mockito.when(userContextUtils.loggedInUserName()).thenReturn("u1");
        Mockito.doNothing().when(userService).updateUser("u1", updateRequest);
        Assertions.assertDoesNotThrow(() -> {
            ResponseEntity<String> response = userController.updateUser("u1", updateRequest);
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals("User has been updated successfully", response.getBody());
        });

        //Case: Update User which does not exist
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(true);
        Mockito.when(userContextUtils.loggedInUserName()).thenReturn("u1");
        Mockito.doThrow(new EntityNotFoundException("Not found")).when(userService).updateUser("u1", updateRequest);
        Assertions.assertThrows(EntityNotFoundException.class ,() -> {
            userController.updateUser("u1", updateRequest);
        });
    }
}
