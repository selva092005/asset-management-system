package com.learn.demo.dto.response;

import lombok.Data;

@Data
public class CompanyResponseDTO {
    private Long companyId;
    private String companyName;
    private String companyCode; // e.g. "H" from "Hero"
}
