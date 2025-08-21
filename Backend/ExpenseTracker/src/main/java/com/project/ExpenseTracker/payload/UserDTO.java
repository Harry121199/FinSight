package com.project.ExpenseTracker.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.ExpenseTracker.enums.Gender;
import com.project.ExpenseTracker.enums.Roles;
import com.project.ExpenseTracker.model.Budget;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserDTO {

    private Long uid; // useful for responses

    @NotBlank(message = "First name is required")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Only alphabets are allowed")
    private String firstname;

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Only alphabets are allowed")
    private String lastname;

    @NotNull(message = "Gender is required")
    private Gender gender;

    private Roles roles = Roles.MEMBER;


    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private LocalDate createdOn;

    // For responses, include expense details if needed
    private List<ExpenseDTO> expenses;
    private List<BudgetDTO> budgets;
}
