package com.project.ExpenseTracker.payload.dashboard;

import com.project.ExpenseTracker.enums.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategorySpendingDTO {
    private Double totalAmount;
    private ExpenseCategory expenseCategory;
}
