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

    @Pattern(
        regexp = "^$|^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password must be at least 8 characters and include uppercase, lowercase, number, and special character (@$!%*?&)"
    )
    private String userPassword;

    @NotBlank(message = "Role is required")
    @Pattern(
        regexp = "MANAGER|ADMIN|USER",
        message = "Role must be MANAGER, ADMIN or USER"
    )
    private String userRole;

    private String employeeId;

    private String department;

    private String phoneNumber;

    private String designation;
}
