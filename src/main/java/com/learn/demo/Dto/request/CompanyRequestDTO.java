package com.learn.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanyRequestDTO {

    @NotBlank(message = "Company name is required")
    private String companyName;
}
