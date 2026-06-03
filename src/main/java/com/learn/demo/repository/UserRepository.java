package com.learn.demo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.learn.demo.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    // 🔍 Search with pagination + soft delete
    @Query("""
        SELECT u FROM User u
        WHERE u.deleted = false
        AND (:username IS NULL OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :username, '%')))
        AND (:role IS NULL OR u.userRole = :role)
    """)
    Page<User> searchUsers(
        @Param("username") String username,
        @Param("role") String role,
        Pageable pageable
    );

    // ✅ FIXED: was findByUserEmailAndIsDeletedFalse — wrong because field is "deleted" not "isDeleted"
    // With primitive boolean, Lombok generates isDeleted() getter but the JPA field name is "deleted"
    // So the correct derived query uses "DeletedFalse" (matching the field name)
    Optional<User> findByUserEmailAndDeletedFalse(String email);
    Optional<User> findByUserNameAndDeletedFalse(String userName);

    // Check duplicate email — only among active (non-deleted) users
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.userEmail = :email AND u.deleted = false")
    boolean existsByUserEmail(@Param("email") String email);

    long countByDeletedFalse();

    long countByDeletedFalseAndUserRole(String role);
}
