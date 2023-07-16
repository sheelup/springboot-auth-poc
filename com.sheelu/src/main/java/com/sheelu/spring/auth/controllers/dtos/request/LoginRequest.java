package com.sheelu.spring.auth.controllers.dtos.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank(message = "UserName can not be blank")
    @Email
    private String userName;
    @NotBlank(message = "Password can not be blank")
    private String password;
}
