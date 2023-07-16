package com.sheelu.spring.auth.controllers;

import com.sheelu.spring.auth.service.UserService;
import com.sheelu.spring.auth.util.UserContextUtils;
import com.sheelu.spring.auth.controllers.dtos.request.CreateTimezoneDTO;
import com.sheelu.spring.auth.controllers.dtos.request.TimeDiff;
import com.sheelu.spring.auth.controllers.dtos.request.UpdateTimezoneRequest;
import com.sheelu.spring.auth.controllers.dtos.response.TimezoneResponseDTO;
import com.sheelu.spring.auth.exceptions.EntityNotFoundException;
import com.sheelu.spring.auth.exceptions.UnAuthorizedAccessException;
import com.sheelu.spring.auth.service.TimeZoneService;
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

public class TimezoneControllerTest {

    @InjectMocks
    TimezoneController timezoneController;

    @Mock
    private UserService userService;

    @Mock
    private UserContextUtils userContextUtils;

    @Mock
    private TimeZoneService timeZoneService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @SneakyThrows
    @Test
    public void createTimezone() {
        CreateTimezoneDTO timezoneDTO = CreateTimezoneDTO.builder()
                .userName("u1")
                .name("IST")
                .city("New Delhi")
                .diffWithGMT(TimeDiff.builder().hours(5).minutes(30).isAhead(true).build())
                .build();

        //Case: User is not authorized to create
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(false);
        Mockito.when(userContextUtils.loggedInUserName()).thenReturn("u2");
        Assertions.assertThrows(UnAuthorizedAccessException.class, () -> {
            timezoneController.createTimezone(timezoneDTO);
        });

        //Case: Timezone created successfully
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(true);
        Mockito.when(userContextUtils.loggedInUserName()).thenReturn("u1");
        Mockito.doNothing().when(timeZoneService).createTimezone(timezoneDTO);
        Assertions.assertDoesNotThrow(() -> {
            ResponseEntity<String> response = timezoneController.createTimezone(timezoneDTO);
            Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
            Assertions.assertEquals("Timezone has been created successfully",response.getBody());
        });

        //Case: User doesn't exist
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(true);
        Mockito.doThrow(new EntityNotFoundException("User not found")).when(timeZoneService).createTimezone(timezoneDTO);
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            timezoneController.createTimezone(timezoneDTO);
        });
    }

    @SneakyThrows
    @Test
    public void testGetUserTimezones() {
        //Case: User not authorized
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(false);
        Mockito.when(userContextUtils.loggedInUserName()).thenReturn("u2");
        Assertions.assertThrows(UnAuthorizedAccessException.class, () -> {
            timezoneController.getUserTimezones("u1");
        });

        //Case: get user timezone successfully
        List<TimezoneResponseDTO> timezones = Arrays.asList(TimezoneResponseDTO.builder().timezoneId("tz1").build());
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(true);
        Mockito.when(userContextUtils.loggedInUserName()).thenReturn("u1");
        Mockito.when(timeZoneService.getTimezonesForUser("u1")).thenReturn(timezones);
        Assertions.assertDoesNotThrow(() -> {
            ResponseEntity<List<TimezoneResponseDTO>> response =
                    timezoneController.getUserTimezones("u1");
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals(timezones,response.getBody());
        });

        //Case: User doesn't exist
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(true);
        Mockito.doThrow(new EntityNotFoundException("User not found")).when(timeZoneService).getTimezonesForUser("u1");
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            timezoneController.getUserTimezones("u1");
        });
    }

    @SneakyThrows
    @Test
    public void testGetTimezone() {
        String timezoneId = "dfgwefg";
        //Case: User Not Authorized
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(false);
        Mockito.when(userContextUtils.loggedInUserName()).thenReturn("u1");
        Mockito.when(timeZoneService.isTimezoneOwnedByUser(timezoneId, "u1")).thenReturn(false);
        Assertions.assertThrows(UnAuthorizedAccessException.class, () -> {
            timezoneController.getTimezone(timezoneId);
        });

        //Case: Get timezone successfully
        TimezoneResponseDTO tz = TimezoneResponseDTO.builder().timezoneId(timezoneId).build();
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(false);
        Mockito.when(userContextUtils.loggedInUserName()).thenReturn("u1");
        Mockito.when(timeZoneService.isTimezoneOwnedByUser(timezoneId, "u1")).thenReturn(true);
        Mockito.when(timeZoneService.getTimezoneById(timezoneId)).thenReturn(tz);
        Assertions.assertDoesNotThrow(() -> {
            ResponseEntity<TimezoneResponseDTO> response = timezoneController.getTimezone(timezoneId);
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals(tz, response.getBody());
        });

        //Case: Timezone doesn't exist with given timezoneId
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(true);
        Mockito.when(timeZoneService.getTimezoneById(timezoneId)).thenThrow(new EntityNotFoundException("Tz not found"));
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            timezoneController.getTimezone(timezoneId);
        });
    }

    @SneakyThrows
    @Test
    public void testDeleteTimezone() {
        String timezoneId = "dfgwefg";
        //Case: User Not Authorized
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(false);
        Mockito.when(userContextUtils.loggedInUserName()).thenReturn("u1");
        Mockito.when(timeZoneService.isTimezoneOwnedByUser(timezoneId, "u1")).thenReturn(false);
        Assertions.assertThrows(UnAuthorizedAccessException.class, () -> {
            timezoneController.deleteTimezone(timezoneId);
        });

        //Case: Delete timezone successfully
        TimezoneResponseDTO tz = TimezoneResponseDTO.builder().timezoneId(timezoneId).build();
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(false);
        Mockito.when(userContextUtils.loggedInUserName()).thenReturn("u1");
        Mockito.when(timeZoneService.isTimezoneOwnedByUser(timezoneId, "u1")).thenReturn(true);
        Mockito.doNothing().when(timeZoneService).deleteTimezoneById(timezoneId);
        Assertions.assertDoesNotThrow(() -> {
            ResponseEntity<String> response = timezoneController.deleteTimezone(timezoneId);
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals("Timezone has been deleted successfully", response.getBody());
        });

        //Case: Timezone doesn't exist with given timezoneId
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(true);
        Mockito.doThrow(new EntityNotFoundException("Tz not found")).when(timeZoneService).deleteTimezoneById(timezoneId);
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            timezoneController.deleteTimezone(timezoneId);
        });
    }

    @SneakyThrows
    @Test
    public void testUpdateTimezone() {
        String timezoneId = "dfgwefg";
        UpdateTimezoneRequest updateRequest =
                UpdateTimezoneRequest.builder().city("New Jesry").name("PST").build();
        //Case: User Not Authorized
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(false);
        Mockito.when(userContextUtils.loggedInUserName()).thenReturn("u1");
        Mockito.when(timeZoneService.isTimezoneOwnedByUser(timezoneId, "u1")).thenReturn(false);
        Assertions.assertThrows(UnAuthorizedAccessException.class, () -> {
            timezoneController.updateTimezone(timezoneId, updateRequest);
        });

        //Case: Delete timezone successfully
        TimezoneResponseDTO tz = TimezoneResponseDTO.builder().timezoneId(timezoneId).build();
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(false);
        Mockito.when(userContextUtils.loggedInUserName()).thenReturn("u1");
        Mockito.when(timeZoneService.isTimezoneOwnedByUser(timezoneId, "u1")).thenReturn(true);
        Mockito.doNothing().when(timeZoneService).updateTimezone(timezoneId, updateRequest);
        Assertions.assertDoesNotThrow(() -> {
            ResponseEntity<String> response = timezoneController.updateTimezone(timezoneId, updateRequest);
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals("Timezone has been updated successfully", response.getBody());
        });

        //Case: Timezone doesn't exist with given timezoneId
        Mockito.when(userContextUtils.isAdminUser()).thenReturn(true);
        Mockito.doThrow(new EntityNotFoundException("Tz not found")).when(timeZoneService).updateTimezone(timezoneId, updateRequest);
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            timezoneController.updateTimezone(timezoneId, updateRequest);
        });
    }
}
