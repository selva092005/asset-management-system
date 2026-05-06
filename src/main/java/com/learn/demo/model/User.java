package com.learn.demo.model;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE user SET deleted = true WHERE user_id = ?")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String userName;

    @Column(unique = true)
    private String userEmail;

    private String userPassword;

    private String userRole;

    // ✅ Use primitive boolean (not Boolean wrapper) for consistency
    // This makes Lombok generate isDeleted() getter, which matches
    // the repository method findByUserEmailAndDeletedFalse()
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @PrePersist
    public void prePersist() {
        this.deleted = false;
    }
}
