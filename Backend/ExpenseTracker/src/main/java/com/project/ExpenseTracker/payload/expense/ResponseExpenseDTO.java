package com.project.ExpenseTracker.payload.expense;

import com.project.ExpenseTracker.enums.ExpenseCategory;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ResponseExpenseDTO {
    private Long eid;
    private String itemName;
    private String description;
    private ExpenseCategory expenseCategory;
    private Double amount;
    private LocalDate transactionDate;
}
