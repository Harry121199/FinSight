package com.project.ExpenseTracker.payload.user;

import com.project.ExpenseTracker.enums.Gender;
import com.project.ExpenseTracker.enums.Roles;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ResponseUserDTO {

    private Long uid;
    private String firstname;
    private String lastname;
    private Gender gender;
    private String email;
    private Roles roles;
    private LocalDate createdOn;
}
