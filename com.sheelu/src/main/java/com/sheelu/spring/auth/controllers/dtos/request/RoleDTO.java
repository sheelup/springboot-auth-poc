package com.sheelu.spring.auth.controllers.dtos.request;

import com.sheelu.spring.auth.models.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
    @NotBlank(message = "role name can't be blank")
    private UserRole roleName;
}
