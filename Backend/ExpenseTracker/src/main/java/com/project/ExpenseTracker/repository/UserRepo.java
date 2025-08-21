package com.project.ExpenseTracker.repository;

import com.project.ExpenseTracker.model.Users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users,Long>, JpaSpecificationExecutor<Users> {
    boolean existsByEmail(@Email(message = "Email should be valid") @NotBlank(message = "Email is required") String email);

    Optional<Users> findByEmail(String username);
}
