package com.sheelu.spring.auth.controllers.dtos.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeDiff {
    @NotNull(message = "Hours can not be null")
    private Integer hours;

    @NotNull(message = "Minutes can not be null")
    private Integer minutes;

    @NotNull(message = "IsAhead can not be null")
    private Boolean isAhead;
}
