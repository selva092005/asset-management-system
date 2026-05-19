package com.learn.demo.mapper;

import com.learn.demo.dto.request.UserRequestDTO;
import com.learn.demo.dto.response.UserResponseDTO;
import com.learn.demo.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // RequestDTO → Entity
    public User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setUserName(dto.getUserName());
        user.setUserEmail(dto.getUserEmail());
        user.setUserPassword(dto.getUserPassword());
        user.setUserRole(dto.getUserRole());
        return user;
    }

    // Entity → ResponseDTO
    public UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setUserName(user.getUserName());
        dto.setUserEmail(user.getUserEmail());
        dto.setUserRole(user.getUserRole());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    // Apply updates from RequestDTO to existing Entity (for PUT)
    // NOTE: password is NOT set here — service handles encoding separately
    public void updateEntityFromDTO(UserRequestDTO dto, User user) {
        user.setUserName(dto.getUserName());
        user.setUserEmail(dto.getUserEmail());
        user.setUserRole(dto.getUserRole());
    }
}
