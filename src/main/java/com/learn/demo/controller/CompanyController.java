package com.learn.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learn.demo.dto.request.CompanyRequestDTO;
import com.learn.demo.dto.response.Apiresponse;
import com.learn.demo.service.CompanyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    // MANAGER only — add company
    @PostMapping
    public ResponseEntity<Apiresponse> save(@Valid @RequestBody CompanyRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new Apiresponse(HttpStatus.CREATED.value(), "Company created", companyService.saveCompany(dto))
        );
    }

    // MANAGER + ADMIN — view all
    @GetMapping
    public ResponseEntity<Apiresponse> getAll() {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "All companies", companyService.getAllCompanies())
        );
    }

    // MANAGER + ADMIN — view by id
    @GetMapping("/{id}")
    public ResponseEntity<Apiresponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Company found", companyService.getCompanyById(id))
        );
    }

    // MANAGER only — update
    @PutMapping("/{id}")
    public ResponseEntity<Apiresponse> update(@PathVariable Long id, @Valid @RequestBody CompanyRequestDTO dto) {
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Company updated", companyService.updateCompany(id, dto))
        );
    }

    // MANAGER only — delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Apiresponse> delete(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok(
            new Apiresponse(HttpStatus.OK.value(), "Company deleted", null)
        );
    }
}
