package com.project.ExpenseTracker.payload.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.ExpenseTracker.enums.Gender;
import com.project.ExpenseTracker.enums.Roles;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RequestUserDTO {

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
    @Size(min = 6, message = "password must be 6 characters long")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private LocalDate createdOn;

}
