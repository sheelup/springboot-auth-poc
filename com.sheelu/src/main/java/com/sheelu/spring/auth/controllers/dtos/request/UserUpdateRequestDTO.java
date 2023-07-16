package com.sheelu.spring.auth.controllers.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequestDTO {
    @NotBlank(message = "FirstName can't be blank")
    private String firstName;
    @NotBlank(message = "LastName can't be blank")
    private String lastName;
}
