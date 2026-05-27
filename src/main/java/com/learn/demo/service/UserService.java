package com.learn.demo.service;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.learn.demo.dto.request.LoginRequest;
import com.learn.demo.dto.request.RefreshTokenRequest;
import com.learn.demo.dto.request.UserRequestDTO;
import com.learn.demo.dto.response.BulkUploadResultDTO;
import com.learn.demo.dto.response.LoginResponse;
import com.learn.demo.dto.response.UserResponseDTO;

public interface UserService {

    UserResponseDTO saveUser(UserRequestDTO dto);

    List<UserResponseDTO> saveUsers(List<UserRequestDTO> dtos);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(Long userId);

    UserResponseDTO updateUser(Long userId, UserRequestDTO dto);

    void deleteUser(Long userId, String adminName);

    Page<UserResponseDTO> searchUsers(String username, String role, int page, int size);

    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(RefreshTokenRequest request);

    BulkUploadResultDTO bulkUploadFromExcel(MultipartFile file);

    ByteArrayOutputStream exportToExcel();

    ByteArrayOutputStream generateTemplate();

    java.util.Map<String, Long> getUserSummaryStats();
}
