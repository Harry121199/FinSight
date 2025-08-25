package com.project.ExpenseTracker.payload.budget;

import com.project.ExpenseTracker.enums.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetSummaryResponse {
    private ExpenseCategory expenseCategory;

    private Double budgetAmount;

    private Double spendAmount;

    private Double remainingAmount;
}
