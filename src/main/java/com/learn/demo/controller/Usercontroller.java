package com.learn.demo.controller;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.learn.demo.dto.request.UserRequestDTO;
import com.learn.demo.dto.response.Apiresponse;
import com.learn.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class Usercontroller {

    private final UserService service;

    // CREATE SINGLE
    @PostMapping
    public ResponseEntity<Apiresponse> saveUser(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new Apiresponse(HttpStatus.CREATED.value(), "User created successfully", service.saveUser(dto))
        );
    }

    // CREATE BULK (JSON)
    @PostMapping("/bulk")
    public ResponseEntity<Apiresponse> saveUsers(@Valid @RequestBody List<UserRequestDTO> dtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new Apiresponse(HttpStatus.CREATED.value(), "Bulk users created successfully", service.saveUsers(dtos))
        );
    }

    // BULK UPLOAD FROM EXCEL
    @PostMapping(value = "/bulk-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Apiresponse> bulkUploadExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty())
            return ResponseEntity.badRequest().body(new Apiresponse(400, "Uploaded file is empty", null));

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls")))
            return ResponseEntity.badRequest().body(new Apiresponse(400, "Only .xlsx and .xls files are supported", null));

        return ResponseEntity.ok(new Apiresponse(HttpStatus.OK.value(), "Bulk upload processed", service.bulkUploadFromExcel(file)));
    }

    // EXPORT ALL USERS TO EXCEL
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportUsers() {
        try {
            ByteArrayOutputStream out = service.exportToExcel();
            byte[] bytes = out.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDisposition(ContentDisposition.attachment().filename("users_export.xlsx").build());
            headers.setContentLength(bytes.length);
            return ResponseEntity.ok().headers(headers).body(bytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // DOWNLOAD BLANK UPLOAD TEMPLATE
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            ByteArrayOutputStream out = service.generateTemplate();
            byte[] bytes = out.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDisposition(ContentDisposition.attachment().filename("user_upload_template.xlsx").build());
            headers.setContentLength(bytes.length);
            return ResponseEntity.ok().headers(headers).body(bytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<Apiresponse> getAllUsers() {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Users retrieved successfully", service.getAllUsers())
        );
    }

    // READ BY ID
    @GetMapping("/{userId}")
    public ResponseEntity<Apiresponse> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "User retrieved successfully", service.getUserById(userId))
        );
    }

    // UPDATE
    @PutMapping("/{userId}")
    public ResponseEntity<Apiresponse> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "User updated successfully", service.updateUser(userId, dto))
        );
    }

    // DELETE
    @DeleteMapping("/{userId}")
    public ResponseEntity<Apiresponse> deleteUser(@PathVariable Long userId) {
        String deletedBy = SecurityContextHolder.getContext().getAuthentication().getName();
        service.deleteUser(userId, deletedBy);
        return ResponseEntity.ok(new Apiresponse(HttpStatus.OK.value(), "Deleted successfully", null));
    }

    // SEARCH + PAGINATION
    @GetMapping("/search/page")
    public ResponseEntity<Apiresponse> searchUsersWithPagination(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Users fetched with pagination",
                service.searchUsers(username, role, page, size))
        );
    }
}
