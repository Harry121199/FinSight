package com.project.ExpenseTracker.payload.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserNameDTO {
    @NotNull(message = "email cannot be empty")
    @Email(message = "Provide a valid email id")
    private String email;

}
