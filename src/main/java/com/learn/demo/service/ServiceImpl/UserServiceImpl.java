package com.learn.demo.service.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.learn.demo.config.JwtUtil;
import com.learn.demo.dto.request.LoginRequest;
import com.learn.demo.dto.request.UserRequestDTO;
import com.learn.demo.dto.response.LoginResponse;
import com.learn.demo.dto.response.UserResponseDTO;
import com.learn.demo.exception.DuplicateResourceException;
import com.learn.demo.exception.InvalidCredentialsException;
import com.learn.demo.exception.ResourceNotFoundException;
import com.learn.demo.mapper.UserMapper;
import com.learn.demo.model.User;
import com.learn.demo.repository.UserRepository;
import com.learn.demo.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // ─────────────────────────────────────────────
    // LOGIN
    // ─────────────────────────────────────────────
    @Override
    public LoginResponse login(LoginRequest request) {
        User user = repository.findByUserEmailAndDeletedFalse(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getUserPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getUserEmail(), user.getUserRole());
        return new LoginResponse(token, user.getUserRole());
    }

    // ─────────────────────────────────────────────
    // CREATE SINGLE USER
    // ─────────────────────────────────────────────
    @Override
    public UserResponseDTO saveUser(UserRequestDTO dto) {
        if (repository.existsByUserEmail(dto.getUserEmail())) {
            throw new DuplicateResourceException("User", "email", dto.getUserEmail());
        }
        User user = userMapper.toEntity(dto);

        // ✅ FIX: Convert role to UPPERCASE — "admin" → "ADMIN", "manager" → "MANAGER"
        user.setUserRole(dto.getUserRole().toUpperCase());

        // ✅ Encode password before saving
        user.setUserPassword(passwordEncoder.encode(dto.getUserPassword()));

        return userMapper.toResponseDTO(repository.save(user));
    }

    // ─────────────────────────────────────────────
    // CREATE BULK USERS
    // ─────────────────────────────────────────────
    @Override
    public List<UserResponseDTO> saveUsers(List<UserRequestDTO> dtos) {
        List<User> users = dtos.stream()
                .map(dto -> {
                    if (repository.existsByUserEmail(dto.getUserEmail())) {
                        throw new DuplicateResourceException("User", "email", dto.getUserEmail());
                    }
                    User user = userMapper.toEntity(dto);

                    // ✅ FIX: Convert role to UPPERCASE in bulk too
                    user.setUserRole(dto.getUserRole().toUpperCase());

                    // ✅ Encode password for each user in bulk
                    user.setUserPassword(passwordEncoder.encode(dto.getUserPassword()));

                    return user;
                })
                .collect(Collectors.toList());

        return repository.saveAll(users)
                .stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // GET ALL USERS
    // ─────────────────────────────────────────────
    @Override
    public List<UserResponseDTO> getAllUsers() {
        return repository.findAll()
                .stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // GET BY ID
    // ─────────────────────────────────────────────
    @Override
    public UserResponseDTO getUserById(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        return userMapper.toResponseDTO(user);
    }

    // ─────────────────────────────────────────────
    // UPDATE USER
    // ─────────────────────────────────────────────
    @Override
    public UserResponseDTO updateUser(Long userId, UserRequestDTO dto) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (!user.getUserEmail().equals(dto.getUserEmail()) &&
                repository.existsByUserEmail(dto.getUserEmail())) {
            throw new DuplicateResourceException("User", "email", dto.getUserEmail());
        }

        userMapper.updateEntityFromDTO(dto, user);

        // ✅ FIX: Convert role to UPPERCASE on update too
        user.setUserRole(dto.getUserRole().toUpperCase());

        // ✅ Encode updated password
        user.setUserPassword(passwordEncoder.encode(dto.getUserPassword()));

        return userMapper.toResponseDTO(repository.save(user));
    }

    // ─────────────────────────────────────────────
    // DELETE USER (soft delete)
    // ─────────────────────────────────────────────
    @Override
    public void deleteUser(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        user.setDeleted(true);
        repository.save(user);
    }

    // ─────────────────────────────────────────────
    // SEARCH WITH PAGINATION
    // ─────────────────────────────────────────────
    @Override
    public Page<UserResponseDTO> searchUsers(String username, String role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // ✅ FIX: Convert role search param to UPPERCASE too
        String roleUpper = (role != null) ? role.toUpperCase() : null;

        return repository.searchUsers(username, roleUpper, pageable)
                .map(userMapper::toResponseDTO);
    }
}