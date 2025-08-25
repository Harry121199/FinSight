package com.project.ExpenseTracker.payload.expense;

import com.project.ExpenseTracker.enums.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseSummaryResponse {
    private ExpenseCategory expenseCategory;
    private Double spentAmount;
}
