package com.sheelu.spring.auth.controllers.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSignupRequest {
    @NotBlank(message = "UserName can't be blank")
    @Size(min = 8, message = "Username should have at least 8 characters")
    private String userName;

    @NotBlank(message = "FirstName can't be blank")
    private String firstName;

    @NotBlank(message = "LastName can't be blank")
    private String lastName;

    @NotBlank(message = "Password can't be blank")
    @Size(min = 8, message = "Password should have at least 8 characters")
    private String password;
}
