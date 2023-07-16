package com.sheelu.spring.auth.controllers;

import com.sheelu.spring.auth.controllers.dtos.request.UpdateTimezoneRequest;
import com.sheelu.spring.auth.controllers.dtos.response.TimezoneResponseDTO;
import com.sheelu.spring.auth.service.UserService;
import com.sheelu.spring.auth.util.UserContextUtils;
import com.sheelu.spring.auth.controllers.dtos.request.CreateTimezoneDTO;
import com.sheelu.spring.auth.exceptions.EntityNotFoundException;
import com.sheelu.spring.auth.exceptions.UnAuthorizedAccessException;
import com.sheelu.spring.auth.service.TimeZoneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class TimezoneController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserContextUtils userContextUtils;
    private final TimeZoneService timeZoneService;

    @Autowired
    public TimezoneController(UserService userService, UserContextUtils userContextUtils, TimeZoneService timeZoneService) {
        this.userService = userService;
        this.userContextUtils = userContextUtils;
        this.timeZoneService = timeZoneService;
    }

    @PostMapping("/api/v1/timezones")
    public ResponseEntity<String> createTimezone(@RequestBody @Valid CreateTimezoneDTO timezoneDTO) throws EntityNotFoundException {
        logger.info("Got Request to create a timezone entry: {}", timezoneDTO);
        if (!isAuthorizedUser(timezoneDTO.getUserName())) {
            throw new UnAuthorizedAccessException("User is not allowed to create this resource");
        }
        timeZoneService.createTimezone(timezoneDTO);
        logger.info("Timezone has been created successfully for request: {}", timezoneDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Timezone has been created successfully");
    }

    @GetMapping("/api/v1/timezones")
    public ResponseEntity<List<TimezoneResponseDTO>> getUserTimezones(@RequestParam String userName) throws EntityNotFoundException {
        logger.info("Got Request to get timezones of userName: {}", userName);
        if (!isAuthorizedUser(userName)) {
            throw new UnAuthorizedAccessException("User is not allowed to create this resource");
        }
        List<TimezoneResponseDTO> timezones = timeZoneService.getTimezonesForUser(userName);
        return ResponseEntity.ok(timezones);
    }

    @GetMapping("/api/v1/timezones/{timezone_id}")
    public ResponseEntity<TimezoneResponseDTO> getTimezone(@PathVariable(value = "timezone_id") String timezoneId) throws EntityNotFoundException {
        logger.info("Got Request to get timezone for id: {}", timezoneId);
        if (!userContextUtils.isAdminUser() && !timeZoneService.isTimezoneOwnedByUser(timezoneId, userContextUtils.loggedInUserName())) {
            throw new UnAuthorizedAccessException("User is not allowed to access this resource");
        }
        TimezoneResponseDTO timezone = timeZoneService.getTimezoneById(timezoneId);
        return ResponseEntity.ok(timezone);
    }

    @DeleteMapping("/api/v1/timezones/{timezone_id}")
    public ResponseEntity<String> deleteTimezone(@PathVariable(value = "timezone_id") String timezoneId) throws EntityNotFoundException {
        logger.info("Got Request to delete timezone: {}", timezoneId);
        if (!userContextUtils.isAdminUser() && !timeZoneService.isTimezoneOwnedByUser(timezoneId, userContextUtils.loggedInUserName())) {
            throw new UnAuthorizedAccessException("User is not allowed to delete this resource");
        }
       timeZoneService.deleteTimezoneById(timezoneId);
        return ResponseEntity.ok("Timezone has been deleted successfully");
    }

    @PutMapping("/api/v1/timezones/{timezone_id}")
    public ResponseEntity<String> updateTimezone(@PathVariable(value = "timezone_id") String timezoneId,
                                                 @RequestBody @Valid UpdateTimezoneRequest updateTimezoneRequest) throws EntityNotFoundException {
        logger.info("Got Request to update timezoneId: {}, updateRequest: {}", timezoneId, updateTimezoneRequest);
        if (!userContextUtils.isAdminUser() && !timeZoneService.isTimezoneOwnedByUser(timezoneId, userContextUtils.loggedInUserName())) {
            throw new UnAuthorizedAccessException("User is not allowed to update this resource");
        }
        timeZoneService.updateTimezone(timezoneId, updateTimezoneRequest);
        return ResponseEntity.ok("Timezone has been updated successfully");
    }

    private boolean isAuthorizedUser(String userName) {
        if (userContextUtils.isAdminUser()) return true;
        return userContextUtils.loggedInUserName().equals(userName);
    }
}
