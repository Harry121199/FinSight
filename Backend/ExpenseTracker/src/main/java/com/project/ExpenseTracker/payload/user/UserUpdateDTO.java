package com.project.ExpenseTracker.payload.user;

import com.project.ExpenseTracker.enums.Gender;
import com.project.ExpenseTracker.enums.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class UserUpdateDTO {

    @Pattern(regexp = "^[a-zA-Z]+$",message = "Only alphabets are allowed")
    private String firstname;

    @Pattern(regexp = "^[a-zA-Z]+$",message = "Only alphabets are allowed")
    private String lastname;

    private Gender gender;

    private Roles roles;

    @Email(message = "Email should be valid")
    private String email;

    private String password;

}
