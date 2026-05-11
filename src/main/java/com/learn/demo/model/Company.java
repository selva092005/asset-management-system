package com.learn.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "company")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted = false")
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long companyId;

    @Column(nullable = false, unique = true)
    private String companyName;
}
