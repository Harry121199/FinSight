package com.project.ExpenseTracker.payload.user;

import com.project.ExpenseTracker.enums.Gender;
import com.project.ExpenseTracker.enums.Roles;
import com.project.ExpenseTracker.model.Budget;
import com.project.ExpenseTracker.model.Expense;
import com.project.ExpenseTracker.model.ExpenseGroup;
import com.project.ExpenseTracker.payload.budget.ResponseBudgetDTO;
import com.project.ExpenseTracker.payload.expense.ResponseExpenseDTO;
import com.project.ExpenseTracker.payload.expenseGroup.ResponseGroupDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class ResponseUserDTO {

    private Long uid;
    private String firstname;
    private String lastname;
    private Gender gender;
    private String email;
    private Roles roles;
    private LocalDate createdOn;

    private List<ResponseExpenseDTO> expenses = new ArrayList<>();
    private List<ResponseBudgetDTO> budgets = new ArrayList<>();
    private List<ResponseGroupDTO> createdExpenseGroups = new ArrayList<>();
    private List<ResponseGroupDTO> expenseGroups = new ArrayList<>();
}
