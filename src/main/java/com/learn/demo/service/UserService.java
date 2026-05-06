package com.learn.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.learn.demo.dto.request.LoginRequest;
import com.learn.demo.dto.request.UserRequestDTO;
import com.learn.demo.dto.response.LoginResponse;
import com.learn.demo.dto.response.UserResponseDTO;

@Service
public interface UserService {

    UserResponseDTO saveUser(UserRequestDTO dto);

    List<UserResponseDTO> saveUsers(List<UserRequestDTO> dtos);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(Long userId);

    UserResponseDTO updateUser(Long userId, UserRequestDTO dto);

    void deleteUser(Long userId);

    Page<UserResponseDTO> searchUsers(String username, String role, int page, int size);

    LoginResponse login(LoginRequest request);
}
