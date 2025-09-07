package com.project.ExpenseTracker.payload.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MonthlyTrendDTO {
    private String month;
    private Double amount;
}
