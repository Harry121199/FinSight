package com.project.ExpenseTracker.payload.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDeleteRequest {
    @NotNull(message = "Password cannot be empty")
    private String password;
}
