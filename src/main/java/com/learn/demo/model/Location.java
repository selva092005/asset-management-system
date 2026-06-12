package com.learn.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "location")
@Data
@EqualsAndHashCode(callSuper = false)
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

    @Column(name = "latitude", precision = 9, scale = 6)
    private java.math.BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6)
    private java.math.BigDecimal longitude;

    @Column(name = "location_type")
    private String locationType;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "contact_person")
    private String contactPerson;
}
