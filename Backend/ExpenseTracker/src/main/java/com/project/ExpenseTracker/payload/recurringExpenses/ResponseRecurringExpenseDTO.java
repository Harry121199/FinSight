package com.project.ExpenseTracker.payload.recurringExpenses;

import com.project.ExpenseTracker.enums.ExpenseCategory;
import com.project.ExpenseTracker.enums.Frequency;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ResponseRecurringExpenseDTO {
    private Long rid;
    private String itemName;
    private String description;
    private ExpenseCategory expenseCategory;
    private Double amount;
    private Frequency frequency;
    private int dayOfMonth;
    private LocalDate startDate;
    private LocalDate endDate;
}
