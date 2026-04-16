package com.learn.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.learn.demo.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

@Query("SELECT u FROM User u WHERE " +
       "(:username IS NULL OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
       "(:role IS NULL OR LOWER(u.userRole) = LOWER(:role))")
List<User> searchUsers(@Param("username") String username,
                       @Param("role") String role);
}
