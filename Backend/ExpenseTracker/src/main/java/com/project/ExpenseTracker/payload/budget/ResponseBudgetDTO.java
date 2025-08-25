package com.project.ExpenseTracker.payload.budget;

import com.project.ExpenseTracker.enums.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBudgetDTO {
    private Long bid;
    private ExpenseCategory expenseCategory;
    private Double amount;
    private String period;
}
