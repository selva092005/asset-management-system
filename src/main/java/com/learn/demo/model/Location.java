package com.learn.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "location")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted = false")
public class Location extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long locationId;

    @Column(nullable = false)
    private String locationName;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
}
