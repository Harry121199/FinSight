package com.project.ExpenseTracker.payload.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DashBoardDTO {
    private Double totalSpendingThisMonth;
    private Double totalSpendingLastMonth;
    private List<CategorySpendingDTO> spendingByCategoryThisMonth;
}
