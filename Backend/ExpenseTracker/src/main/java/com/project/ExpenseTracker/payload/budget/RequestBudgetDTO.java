package com.project.ExpenseTracker.payload.budget;

import com.project.ExpenseTracker.enums.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RequestBudgetDTO {
    @NotNull(message = "Category is mandatory")
    private ExpenseCategory expenseCategory;

    @NotNull(message = "Amount is mandatory")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private Double amount;

    @NotBlank(message = "Period is mandatory")
    @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "Period must be in YYYY-MM format")
    private String period;
}
