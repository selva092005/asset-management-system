package com.learn.demo.dto.response;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long userId;
    private String userName;
    private String userEmail;
    private String userRole;
}
