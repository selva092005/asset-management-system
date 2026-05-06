package com.learn.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRequestDTO {

    @NotBlank(message = "Username is required")
    private String userName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String userEmail;

    @NotBlank(message = "Password is required")
    private String userPassword;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "ADMIN|USER", message = "Role must be ADMIN or USER")
    private String userRole;
}
