package com.sheelu.spring.auth.mappers;

import com.sheelu.spring.auth.controllers.dtos.request.CreateTimezoneDTO;
import com.sheelu.spring.auth.controllers.dtos.response.TimezoneResponseDTO;
import com.sheelu.spring.auth.models.Timezone;
import org.springframework.stereotype.Component;

@Component
public class TimezoneMapper {
    public void mapToTimezoneEntity(CreateTimezoneDTO timezoneDTO, Timezone timezoneEntity) {
        timezoneEntity.setName(timezoneDTO.getName());
        timezoneEntity.setCity(timezoneDTO.getCity());
        timezoneEntity.setDiffHours(timezoneDTO.getDiffWithGMT().getHours());
        timezoneEntity.setDiffMinutes(timezoneDTO.getDiffWithGMT().getMinutes());
        timezoneEntity.setIsAheadOfGMT(timezoneDTO.getDiffWithGMT().getIsAhead());
    }

    public void mapToTimezoneResponseDTO(Timezone timezone, TimezoneResponseDTO timezoneResponseDTO) {
        timezoneResponseDTO.setUserName(timezone.getUser().getUserName());
        timezoneResponseDTO.setName(timezone.getName());
        timezoneResponseDTO.setCity(timezone.getCity());
        timezoneResponseDTO.setTimezoneId(timezone.getExternalId());
        TimezoneResponseDTO.TimeDiff timeDiff = new TimezoneResponseDTO.TimeDiff();
        timeDiff.setHours(timezone.getDiffHours());
        timeDiff.setMinutes(timezone.getDiffMinutes());
        timeDiff.setIsAhead(timezone.getIsAheadOfGMT());
        timezoneResponseDTO.setDiffWithGMT(timeDiff);
    }
}
