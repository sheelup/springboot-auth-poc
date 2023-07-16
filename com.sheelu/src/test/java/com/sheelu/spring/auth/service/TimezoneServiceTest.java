package com.sheelu.spring.auth.service;

import com.sheelu.spring.auth.dao.UserRepository;
import com.sheelu.spring.auth.models.User;
import com.sheelu.spring.auth.util.UUIDGeneratorUtils;
import com.sheelu.spring.auth.controllers.dtos.request.CreateTimezoneDTO;
import com.sheelu.spring.auth.controllers.dtos.request.TimeDiff;
import com.sheelu.spring.auth.controllers.dtos.request.UpdateTimezoneRequest;
import com.sheelu.spring.auth.controllers.dtos.response.TimezoneResponseDTO;
import com.sheelu.spring.auth.dao.TimezoneRepository;
import com.sheelu.spring.auth.exceptions.BadRequest;
import com.sheelu.spring.auth.exceptions.EntityNotFoundException;
import com.sheelu.spring.auth.mappers.TimezoneMapper;
import com.sheelu.spring.auth.models.Timezone;
import com.sheelu.spring.auth.service.impl.TimezoneServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class TimezoneServiceTest {

    @InjectMocks
    TimezoneServiceImpl timezoneService;


    @Spy
    private TimezoneMapper timezoneMapper;

    @Spy
    private UUIDGeneratorUtils uuidGeneratorUtils;

    @Mock
    private TimezoneRepository timezoneRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateTimezone() {
        //Case: Create timezone successfully
        CreateTimezoneDTO timezoneDTO = CreateTimezoneDTO.builder()
                .userName("u1")
                .name("IST")
                .city("New Delhi")
                .diffWithGMT(TimeDiff.builder().hours(5).minutes(30).isAhead(true).build())
                .build();
        Mockito.when(userRepository.findByUserName("u1"))
                .thenReturn(Optional.of(User.builder().id(100L).userName("u1").build()));
        Mockito.when(timezoneRepository.save(Mockito.any())).thenReturn(new Timezone());
        Assertions.assertDoesNotThrow(() ->
                timezoneService.createTimezone(timezoneDTO)
        );

        //Case: Create successfully when timezone behind GMT with max allowed
        CreateTimezoneDTO timezoneDTO6 = CreateTimezoneDTO.builder()
                .userName("u1")
                .name("IST")
                .city("New Delhi")
                .diffWithGMT(TimeDiff.builder().hours(11).minutes(59).isAhead(false).build())
                .build();
        Mockito.when(userRepository.findByUserName("u1"))
                .thenReturn(Optional.of(User.builder().id(100L).userName("u1").build()));
        Mockito.when(timezoneRepository.save(Mockito.any())).thenReturn(new Timezone());
        Assertions.assertDoesNotThrow(() ->
                timezoneService.createTimezone(timezoneDTO6)
        );

        //Case: Create successfully when timezone behind GMT with max allowed
        CreateTimezoneDTO timezoneDTO7 = CreateTimezoneDTO.builder()
                .userName("u1")
                .name("IST")
                .city("New Delhi")
                .diffWithGMT(TimeDiff.builder().hours(12).minutes(0).isAhead(true).build())
                .build();
        Mockito.when(userRepository.findByUserName("u1"))
                .thenReturn(Optional.of(User.builder().id(100L).userName("u1").build()));
        Mockito.when(timezoneRepository.save(Mockito.any())).thenReturn(new Timezone());
        Assertions.assertDoesNotThrow(() ->
                timezoneService.createTimezone(timezoneDTO7)
        );

        //Case: Fail when username does not exist
        CreateTimezoneDTO timezoneDTO1 = CreateTimezoneDTO.builder()
                .userName("u1")
                .name("IST")
                .city("New Delhi")
                .diffWithGMT(TimeDiff.builder().hours(5).minutes(30).isAhead(true).build())
                .build();
        Mockito.when(userRepository.findByUserName("u1"))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                timezoneService.createTimezone(timezoneDTO1)
        );

        //Case: Fail when hours in timeDiff is invalid
        CreateTimezoneDTO timezoneDTO2 = CreateTimezoneDTO.builder()
                .userName("u1")
                .name("IST")
                .city("New Delhi")
                .diffWithGMT(TimeDiff.builder().hours(13).minutes(30).isAhead(true).build())
                .build();
        Mockito.when(userRepository.findByUserName("u1"))
                .thenReturn(Optional.of(User.builder().id(100L).userName("u1").build()));
        Assertions.assertThrows(BadRequest.class, () ->
                timezoneService.createTimezone(timezoneDTO2)
        );

        //Case: Fail when minutes in timeDiff is invalid
        CreateTimezoneDTO timezoneDTO3 = CreateTimezoneDTO.builder()
                .userName("u1")
                .name("IST")
                .city("New Delhi")
                .diffWithGMT(TimeDiff.builder().hours(11).minutes(61).isAhead(true).build())
                .build();
        Mockito.when(userRepository.findByUserName("u1"))
                .thenReturn(Optional.of(User.builder().id(100L).userName("u1").build()));
        Assertions.assertThrows(BadRequest.class, () ->
                timezoneService.createTimezone(timezoneDTO3)
        );

        //Case: Fail when timeDiff exceeds and timzone behind GMT
        CreateTimezoneDTO timezoneDTO4 = CreateTimezoneDTO.builder()
                .userName("u1")
                .name("IST")
                .city("New Delhi")
                .diffWithGMT(TimeDiff.builder().hours(12).minutes(0).isAhead(false).build())
                .build();
        Mockito.when(userRepository.findByUserName("u1"))
                .thenReturn(Optional.of(User.builder().id(100L).userName("u1").build()));
        Assertions.assertThrows(BadRequest.class, () ->
                timezoneService.createTimezone(timezoneDTO4)
        );

        //Case: Fail when timeDiff exceeds and timzone ahead of GMT
        CreateTimezoneDTO timezoneDTO5 = CreateTimezoneDTO.builder()
                .userName("u1")
                .name("IST")
                .city("New Delhi")
                .diffWithGMT(TimeDiff.builder().hours(12).minutes(1).isAhead(true).build())
                .build();
        Mockito.when(userRepository.findByUserName("u1"))
                .thenReturn(Optional.of(User.builder().id(100L).userName("u1").build()));
        Assertions.assertThrows(BadRequest.class, () ->
                timezoneService.createTimezone(timezoneDTO5)
        );
    }

    @Test
    public void testGetTimezonesForUser() {
        String userName = "u1";
        User user = User.builder().id(100L).userName("u1").build();

        //Case: User owns no timzone
        user.setTimezones(Collections.emptyList());
        Mockito.when(userRepository.findByUserName(userName)).thenReturn(Optional.of(user));
        Assertions.assertDoesNotThrow(() -> {
            List<TimezoneResponseDTO> result = timezoneService.getTimezonesForUser(userName);
            Assertions.assertEquals(0, result.size());
        });

        //Case: User own one timezone
        user.setTimezones(Arrays.asList(
                Timezone.builder()
                        .id(1L)
                        .name("IST")
                        .city("New Delhi")
                        .diffHours(5)
                        .diffMinutes(30)
                        .isAheadOfGMT(true)
                        .user(user)
                        .build()));
        Mockito.when(userRepository.findByUserName(userName)).thenReturn(Optional.of(user));
        Assertions.assertDoesNotThrow(() -> {
            List<TimezoneResponseDTO> result = timezoneService.getTimezonesForUser(userName);
            Assertions.assertEquals(1, result.size());
            Assertions.assertEquals("u1", result.get(0).getUserName());
            Assertions.assertEquals("IST", result.get(0).getName());
            Assertions.assertEquals("New Delhi", result.get(0).getCity());
            Assertions.assertEquals("GMT+05:30", result.get(0).getTzPrettyString());
        });

        //Case: User multiple timezones
        user.setTimezones(Arrays.asList(
                Timezone.builder()
                        .id(1L)
                        .name("IST")
                        .city("New Delhi")
                        .diffHours(5)
                        .diffMinutes(30)
                        .isAheadOfGMT(true)
                        .user(user)
                        .build(),
                Timezone.builder()
                        .id(2L)
                        .name("PST")
                        .city("New Jersy")
                        .diffHours(4)
                        .diffMinutes(20)
                        .isAheadOfGMT(false)
                        .user(user)
                        .build()
                ));
        Mockito.when(userRepository.findByUserName(userName)).thenReturn(Optional.of(user));
        Assertions.assertDoesNotThrow(() -> {
            List<TimezoneResponseDTO> result = timezoneService.getTimezonesForUser(userName);
            Assertions.assertEquals(2, result.size());

            Assertions.assertEquals("u1", result.get(0).getUserName());
            Assertions.assertEquals("IST", result.get(0).getName());
            Assertions.assertEquals("New Delhi", result.get(0).getCity());
            Assertions.assertEquals("GMT+05:30", result.get(0).getTzPrettyString());

            Assertions.assertEquals("u1", result.get(1).getUserName());
            Assertions.assertEquals("PST", result.get(1).getName());
            Assertions.assertEquals("New Jersy", result.get(1).getCity());
            Assertions.assertEquals("GMT-04:20", result.get(1).getTzPrettyString());
        });

        //Case: Fail when User does not exist
        Mockito.when(userRepository.findByUserName(userName)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class,() -> {
            timezoneService.getTimezonesForUser(userName);
        });
    }

    @Test
    public void testGetTimezoneById() {
        String timezoneId = "zcxvbnvcqwewrt";
        //Case: timezone exist with given timezoneId
        Mockito.when(timezoneRepository.findByExternalId(timezoneId))
                .thenReturn(Optional.of(Timezone.builder()
                        .id(1L)
                        .name("IST")
                        .user(User.builder().id(100L).userName("u1").build())
                        .city("New Delhi")
                        .externalId(timezoneId)
                        .diffHours(5)
                        .diffMinutes(30)
                        .isAheadOfGMT(true)
                        .build()));
        Assertions.assertDoesNotThrow(() -> {
            TimezoneResponseDTO tz = timezoneService.getTimezoneById(timezoneId);
            Assertions.assertEquals("u1", tz.getUserName());
            Assertions.assertEquals("IST", tz.getName());
            Assertions.assertEquals("New Delhi", tz.getCity());
            Assertions.assertEquals("GMT+05:30", tz.getTzPrettyString());
            Assertions.assertEquals(timezoneId, tz.getTimezoneId());
        });

        //Case: timezone does not exist with given timezoneId
        Mockito.when(timezoneRepository.findByExternalId(timezoneId)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            timezoneService.getTimezoneById(timezoneId);
        });
    }

    @Test
    public void testIsTimezoneOwnedByUser() {
        String timezoneId = "zcxvbnvcqwewrt";
        //Case: user owns timezone with given timezoneId
        Mockito.when(timezoneRepository.findByExternalId(timezoneId))
                .thenReturn(Optional.of(Timezone.builder()
                .id(1L)
                .name("IST")
                .user(User.builder().id(100L).userName("u1").build())
                .city("New Delhi")
                .externalId(timezoneId)
                .diffHours(5)
                .diffMinutes(30)
                .isAheadOfGMT(true)
                .build()));
        Assertions.assertDoesNotThrow(() -> {
            Assertions.assertTrue(timezoneService.isTimezoneOwnedByUser(timezoneId, "u1"));
        });

        //Case: User doesn't own timezone with given timezoneId
        Assertions.assertDoesNotThrow(() -> {
            Assertions.assertFalse(timezoneService.isTimezoneOwnedByUser(timezoneId, "u2"));
        });

        //Case: timezone doesn't exist with given timezoneId
        Mockito.when(timezoneRepository.findByExternalId(timezoneId)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            timezoneService.isTimezoneOwnedByUser(timezoneId, "u2");
        });
    }

    @Test
    public void testDeleteTimezoneById() {
        String timezoneId = "zcxvbnvcqwewrt";
        //Case: Delete timezone successfully
        Mockito.when(timezoneRepository.findByExternalId(timezoneId))
                .thenReturn(Optional.of(Timezone.builder()
                        .id(1L)
                        .name("IST")
                        .user(User.builder().id(100L).userName("u1").build())
                        .city("New Delhi")
                        .externalId(timezoneId)
                        .diffHours(5)
                        .diffMinutes(30)
                        .isAheadOfGMT(true)
                        .build()));
        Assertions.assertDoesNotThrow(() -> {
            timezoneService.deleteTimezoneById(timezoneId);
        });

        //Case: timezone doesn't exist with given timezoneId
        Mockito.when(timezoneRepository.findByExternalId(timezoneId)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            timezoneService.deleteTimezoneById(timezoneId);
        });
    }

    @Test
    public void testUpdateTimezone() {
        String timezoneId = "zcxvbnvcqwewrt";
        UpdateTimezoneRequest timezoneDTO = UpdateTimezoneRequest.builder()
                .name("PST")
                .city("New Jersy")
                .diffWithGMT(TimeDiff.builder().hours(4).minutes(10).isAhead(false).build())
                .build();
        Mockito.when(timezoneRepository.findByExternalId(timezoneId))
                .thenReturn(Optional.of(Timezone.builder()
                        .id(1L)
                        .name("IST")
                        .user(User.builder().id(100L).userName("u1").build())
                        .city("New Delhi")
                        .externalId(timezoneId)
                        .diffHours(5)
                        .diffMinutes(30)
                        .isAheadOfGMT(true)
                        .build()));
        Timezone updatedTimezone = Timezone.builder()
                .id(1L)
                .name("PST")
                .user(User.builder().id(100L).userName("u1").build())
                .city("New Jersy")
                .externalId(timezoneId)
                .diffHours(4)
                .diffMinutes(10)
                .isAheadOfGMT(false)
                .build();
        Mockito.when(timezoneRepository.save(updatedTimezone)).thenReturn(new Timezone());
        Assertions.assertDoesNotThrow(() ->
                timezoneService.updateTimezone(timezoneId, timezoneDTO)
        );
    }
}
