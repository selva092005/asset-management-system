package com.learn.demo.controller;

import com.learn.demo.dto.request.UserRequestDTO;
import com.learn.demo.dto.response.Apiresponse;
import com.learn.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class Usercontroller {

    private final UserService service;

    // CREATE SINGLE
    @PostMapping
    public ResponseEntity<Apiresponse> saveUser(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new Apiresponse(
                HttpStatus.CREATED.value(),
                "User created successfully",
                service.saveUser(dto)
            )
        );
    }

    // CREATE BULK
    @PostMapping("/bulk")
    public ResponseEntity<Apiresponse> saveUsers(@Valid @RequestBody List<UserRequestDTO> dtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new Apiresponse(
                HttpStatus.CREATED.value(),
                "Bulk users created successfully",
                service.saveUsers(dtos)
            )
        );
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
        service.deleteUser(userId);
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Deleted successfully", null)
        );
    }

    // SEARCH + PAGINATION
    @GetMapping("/search/page")
    public ResponseEntity<Apiresponse> searchUsersWithPagination(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            new Apiresponse(
                HttpStatus.OK.value(),
                "Users fetched with pagination",
                service.searchUsers(username, role, page, size)
            )
        );
    }
}
