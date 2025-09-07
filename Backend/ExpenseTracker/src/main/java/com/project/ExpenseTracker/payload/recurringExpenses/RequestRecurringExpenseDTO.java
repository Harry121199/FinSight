package com.project.ExpenseTracker.payload.recurringExpenses;

import com.project.ExpenseTracker.enums.ExpenseCategory;
import com.project.ExpenseTracker.enums.Frequency;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RequestRecurringExpenseDTO {
    @NotBlank(message = "Item name is mandatory")
    @Size(min = 2, max = 100, message = "Item name must be between 2 and 100 characters")
    private String itemName;

    private String description;

    @NotNull(message = "Category is mandatory")
    private ExpenseCategory expenseCategory;

    @NotNull(message = "Amount is mandatory")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private Double amount;

    @NotNull(message = "Frequency is mandatory")
    private Frequency frequency;

    @NotNull(message = "Day of month is mandatory")
    @Min(value = 1, message = "Day must be at least 1")
    @Max(value = 31, message = "Day must be no more than 31")
    private int dayOfMonth;

    @NotNull(message = "Start date is mandatory")
    private LocalDate startDate;

    private LocalDate endDate;
}
