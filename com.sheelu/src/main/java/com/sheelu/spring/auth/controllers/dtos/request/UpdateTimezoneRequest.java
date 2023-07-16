package com.sheelu.spring.auth.controllers.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateTimezoneRequest {
    @NotBlank(message = "Name can not be blank")
    private String name;

    @NotBlank(message = "City can not be blank")
    private String city;

    @NotNull(message = "DiffWithGMT can not be null")
    private TimeDiff diffWithGMT;
}
