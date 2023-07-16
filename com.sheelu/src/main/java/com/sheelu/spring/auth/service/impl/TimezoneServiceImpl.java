package com.sheelu.spring.auth.service.impl;

import com.sheelu.spring.auth.controllers.dtos.request.CreateTimezoneDTO;
import com.sheelu.spring.auth.controllers.dtos.request.TimeDiff;
import com.sheelu.spring.auth.controllers.dtos.request.UpdateTimezoneRequest;
import com.sheelu.spring.auth.controllers.dtos.response.TimezoneResponseDTO;
import com.sheelu.spring.auth.dao.UserRepository;
import com.sheelu.spring.auth.exceptions.BadRequest;
import com.sheelu.spring.auth.exceptions.EntityNotFoundException;
import com.sheelu.spring.auth.models.User;
import com.sheelu.spring.auth.util.UUIDGeneratorUtils;
import com.sheelu.spring.auth.dao.TimezoneRepository;
import com.sheelu.spring.auth.mappers.TimezoneMapper;
import com.sheelu.spring.auth.models.Timezone;
import com.sheelu.spring.auth.service.TimeZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimezoneServiceImpl implements TimeZoneService {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final TimezoneRepository timezoneRepository;
    private final UserRepository userRepository;
    private final TimezoneMapper timezoneMapper;
    private final UUIDGeneratorUtils uuidGeneratorUtils;

    @Autowired
    public TimezoneServiceImpl(TimezoneRepository timezoneRepository, UserRepository userRepository, TimezoneMapper timezoneMapper, UUIDGeneratorUtils uuidGeneratorUtils) {
        this.timezoneRepository = timezoneRepository;
        this.userRepository = userRepository;
        this.timezoneMapper = timezoneMapper;
        this.uuidGeneratorUtils = uuidGeneratorUtils;
    }

    @Override
    public void createTimezone(CreateTimezoneDTO timezoneDTO) throws EntityNotFoundException {
        this.validateTimezoneDiff(timezoneDTO.getDiffWithGMT());
        User user = userRepository.findByUserName(timezoneDTO.getUserName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Timezone timezoneEntity = new Timezone();
        timezoneMapper.mapToTimezoneEntity(timezoneDTO, timezoneEntity);
        timezoneEntity.setUser(user);
        timezoneEntity.setExternalId(uuidGeneratorUtils.generateUUID());
        timezoneRepository.save(timezoneEntity);
    }

    @Override
    public List<TimezoneResponseDTO> getTimezonesForUser(String userName) throws EntityNotFoundException {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return user.getTimezones().stream()
                .map(timezone -> this.buildTimezoneResponseDTO(timezone))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isTimezoneOwnedByUser(String timezoneId, String loggedInUserName) throws EntityNotFoundException {
        Timezone tz = timezoneRepository.findByExternalId(timezoneId)
                .orElseThrow(() -> new EntityNotFoundException("Timezone not found"));
        return tz.getUser().getUserName().equals(loggedInUserName);
    }

    @Override
    public TimezoneResponseDTO getTimezoneById(String timezoneId) throws EntityNotFoundException {
        Timezone tz = timezoneRepository.findByExternalId(timezoneId)
                .orElseThrow(() -> new EntityNotFoundException("Timezone not found"));
        return buildTimezoneResponseDTO(tz);
    }

    @Override
    public void deleteTimezoneById(String timezoneId) throws EntityNotFoundException {
        Timezone tz = timezoneRepository.findByExternalId(timezoneId)
                .orElseThrow(() -> new EntityNotFoundException("Timezone not found"));
        tz.setUser(null);
        timezoneRepository.delete(tz);
    }

    @Override
    public void updateTimezone(String timezoneId, UpdateTimezoneRequest updateTimezoneRequest) throws EntityNotFoundException {
        validateTimezoneDiff(updateTimezoneRequest.getDiffWithGMT());
        Timezone tz = timezoneRepository.findByExternalId(timezoneId)
                .orElseThrow(() -> new EntityNotFoundException("Timezone not found"));
        tz.setName(updateTimezoneRequest.getName());
        tz.setCity(updateTimezoneRequest.getCity());
        tz.setDiffHours(updateTimezoneRequest.getDiffWithGMT().getHours());
        tz.setDiffMinutes(updateTimezoneRequest.getDiffWithGMT().getMinutes());
        tz.setIsAheadOfGMT(updateTimezoneRequest.getDiffWithGMT().getIsAhead());
        timezoneRepository.save(tz);
    }


    private ZonedDateTime gmtToLocalTime(ZonedDateTime gmtTime, Timezone timezone) {
        if (timezone.getIsAheadOfGMT()) {
            return gmtTime.plusHours(timezone.getDiffHours()).plusMinutes(timezone.getDiffMinutes());
        }
        return gmtTime.minusHours(timezone.getDiffHours().longValue()).minusMinutes(timezone.getDiffMinutes());
    }


    private void validateTimezoneDiff(TimeDiff timeDiff) {
        if (timeDiff.getHours() < 0) {
            throw new BadRequest("Hours value is invalid");
        }
        if (timeDiff.getMinutes() < 0 || timeDiff.getMinutes() > 59) {
            throw new BadRequest("Minutes value is invalid");
        }
        if (timeDiff.getIsAhead()) {
            if (timeDiff.getHours() > 12) throw new BadRequest("Hours value is invalid");
            if (timeDiff.getHours() == 12 && timeDiff.getMinutes() > 0)
                throw new BadRequest("Invalid time diff");
        } else {
            if (timeDiff.getHours() > 11) throw new BadRequest("Hours value is invalid");
        }
    }

    private TimezoneResponseDTO buildTimezoneResponseDTO(Timezone timezone) {
        TimezoneResponseDTO timezoneResponseDTO = new TimezoneResponseDTO();
        timezoneMapper.mapToTimezoneResponseDTO(timezone, timezoneResponseDTO);
        timezoneResponseDTO.setTzPrettyString(timezoneResponseDTO.getDiffWithGMT().prettyString());
        ZonedDateTime gmtTime = ZonedDateTime.now(ZoneOffset.UTC);
        timezoneResponseDTO.setCurrentTimeGMT(gmtTime.format(dateFormatter));
        timezoneResponseDTO.setCurrentTimeLocal(gmtToLocalTime(gmtTime, timezone).format(dateFormatter));
        return timezoneResponseDTO;
    }
}
