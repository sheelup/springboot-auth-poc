package com.sheelu.spring.auth.service;

import com.sheelu.spring.auth.controllers.dtos.request.CreateTimezoneDTO;
import com.sheelu.spring.auth.controllers.dtos.request.UpdateTimezoneRequest;
import com.sheelu.spring.auth.controllers.dtos.response.TimezoneResponseDTO;
import com.sheelu.spring.auth.exceptions.EntityNotFoundException;

import java.util.List;

public interface TimeZoneService {
    void createTimezone(CreateTimezoneDTO timezoneDTO) throws EntityNotFoundException;

    List<TimezoneResponseDTO> getTimezonesForUser(String userName) throws EntityNotFoundException;

    boolean isTimezoneOwnedByUser(String timezoneId, String loggedInUserName) throws EntityNotFoundException;

    TimezoneResponseDTO getTimezoneById(String timezoneId) throws EntityNotFoundException;

    void deleteTimezoneById(String timezoneId) throws EntityNotFoundException;

    void updateTimezone(String timezoneId, UpdateTimezoneRequest updateTimezoneRequest) throws EntityNotFoundException;
}
