package com.learn.demo.service.ServiceImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.learn.demo.config.JwtUtil;
import com.learn.demo.dto.request.LoginRequest;
import com.learn.demo.dto.request.RefreshTokenRequest;
import com.learn.demo.dto.request.UserRequestDTO;
import com.learn.demo.dto.response.BulkUploadResultDTO;
import com.learn.demo.dto.response.BulkUploadResultDTO.RowIssue;
import com.learn.demo.dto.response.LoginResponse;
import com.learn.demo.dto.response.UserResponseDTO;
import com.learn.demo.exception.BusinessRuleException;
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

        String token = jwtUtil.generateToken(user.getUserEmail(), user.getUserRole(), user.getUserName());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserEmail());
        return new LoginResponse(token, refreshToken, user.getUserRole());
    }

    // ─────────────────────────────────────────────
    // REFRESH TOKEN
    // ─────────────────────────────────────────────
    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        jwtUtil.isRefreshTokenValid(request.getRefreshToken());
        String email = jwtUtil.extractEmailFromRefreshToken(request.getRefreshToken());
        User user = repository.findByUserEmailAndDeletedFalse(email)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
        String newToken = jwtUtil.generateToken(user.getUserEmail(), user.getUserRole(), user.getUserName());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUserEmail());
        return new LoginResponse(newToken, newRefreshToken, user.getUserRole());
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
        return repository.searchUsers(null, null, Pageable.unpaged())
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
    public void deleteUser(Long userId, String adminName) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        // Prevent self-deletion
        String loggedInEmail = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        if (user.getUserEmail().equalsIgnoreCase(loggedInEmail)) {
            throw new BusinessRuleException("You cannot delete your own account.");
        }

        user.setDeleted(true);
        user.setDeletedBy(adminName);
        user.setDeletedAt(java.time.LocalDateTime.now());
        repository.save(user);
    }

    // ─────────────────────────────────────────────
    // SEARCH WITH PAGINATION
    // ─────────────────────────────────────────────
    @Override
    public Page<UserResponseDTO> searchUsers(String username, String role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String roleUpper = (role != null) ? role.toUpperCase() : null;
        return repository.searchUsers(username, roleUpper, pageable)
                .map(userMapper::toResponseDTO);
    }

    // ─────────────────────────────────────────────
    // BULK UPLOAD FROM EXCEL
    // ─────────────────────────────────────────────
    @Override
    @Transactional
    public BulkUploadResultDTO bulkUploadFromExcel(MultipartFile file) {
        List<RowIssue> errors  = new ArrayList<>();
        List<RowIssue> skipped = new ArrayList<>();
        int successCount = 0;
        int totalRows    = 0;

        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                totalRows++;
                int rowNum = i + 1;

                try {
                    String userName     = getCellString(row, 0);
                    String userEmail    = getCellString(row, 1);
                    String userPassword = getCellString(row, 2);
                    String userRole     = getCellString(row, 3);

                    if (userName == null || userName.isBlank()) {
                        errors.add(new RowIssue(rowNum, "userName", "User Name is required")); continue;
                    }
                    if (userEmail == null || userEmail.isBlank()) {
                        errors.add(new RowIssue(rowNum, "userEmail", "User Email is required")); continue;
                    }
                    if (!userEmail.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-zA-Z]{2,}$")) {
                        errors.add(new RowIssue(rowNum, "userEmail", "Invalid email format: \"" + userEmail + "\"")); continue;
                    }
                    if (userPassword == null || userPassword.isBlank()) {
                        errors.add(new RowIssue(rowNum, "userPassword", "Password is required")); continue;
                    }
                    if (userRole == null || userRole.isBlank()) {
                        errors.add(new RowIssue(rowNum, "userRole", "Role is required (ADMIN / MANAGER / USER)")); continue;
                    }

                    if (repository.existsByUserEmail(userEmail)) {
                        skipped.add(new RowIssue(rowNum, "userEmail", "Email \"" + userEmail + "\" already exists in the database"));
                        continue;
                    }

                    User user = new User();
                    user.setUserName(userName);
                    user.setUserEmail(userEmail);
                    user.setUserPassword(passwordEncoder.encode(userPassword));
                    user.setUserRole(userRole.toUpperCase());
                    repository.save(user);
                    successCount++;

                } catch (Exception e) {
                    errors.add(new RowIssue(rowNum, "unknown", "Unexpected error: " + e.getMessage()));
                }
            }
        } catch (IOException e) {
            errors.add(new RowIssue(0, "file", "Failed to read Excel file: " + e.getMessage()));
        }

        return new BulkUploadResultDTO(totalRows, successCount, skipped.size(), errors.size(), skipped, errors);
    }

    // ─────────────────────────────────────────────
    // EXPORT TO EXCEL
    // ─────────────────────────────────────────────
    @Override
    public ByteArrayOutputStream exportToExcel() {
        List<User> users = repository.findAll();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);

            String[] headers = { "ID", "Name", "Email", "Role" };
            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(20);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(u.getUserId() != null ? u.getUserId() : 0);
                row.createCell(1).setCellValue(u.getUserName() != null ? u.getUserName() : "");
                row.createCell(2).setCellValue(u.getUserEmail() != null ? u.getUserEmail() : "");
                row.createCell(3).setCellValue(u.getUserRole() != null ? u.getUserRole() : "");
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out;

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel export: " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────
    // GENERATE TEMPLATE
    // ─────────────────────────────────────────────
    @Override
    public ByteArrayOutputStream generateTemplate() {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GOLD.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            String[] headers = { "userName*", "userEmail*", "userPassword*", "userRole* (ADMIN/MANAGER/USER)" };
            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(22);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            CellStyle sampleStyle = workbook.createCellStyle();
            sampleStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            sampleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            sampleStyle.setBorderBottom(BorderStyle.THIN);

            Row sample = sheet.createRow(1);
            String[] sampleData = { "John Doe", "john@example.com", "Password@1", "ADMIN" };
            for (int i = 0; i < sampleData.length; i++) {
                Cell cell = sample.createCell(i);
                cell.setCellValue(sampleData[i]);
                cell.setCellStyle(sampleStyle);
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out;

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate template: " + e.getMessage(), e);
        }
    }

    @Override
    public java.util.Map<String, Long> getUserSummaryStats() {
        java.util.Map<String, Long> stats = new java.util.HashMap<>();
        stats.put("total", repository.countByDeletedFalse());
        stats.put("adminCount", repository.countByDeletedFalseAndUserRole("ADMIN"));
        stats.put("managerCount", repository.countByDeletedFalseAndUserRole("MANAGER"));
        stats.put("userCount", repository.countByDeletedFalseAndUserRole("USER"));
        return stats;
    }

    // ─────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────
    private String getCellString(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case BLANK   -> null;
            default      -> null;
        };
    }
}