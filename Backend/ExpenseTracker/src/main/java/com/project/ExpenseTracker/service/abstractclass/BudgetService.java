package com.project.ExpenseTracker.service.abstractclass;

import com.project.ExpenseTracker.enums.ExpenseCategory;
import com.project.ExpenseTracker.payload.BudgetDTO;
import com.project.ExpenseTracker.payload.BudgetSummaryResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

public interface BudgetService {
    BudgetDTO createBudget(Long uid, @Valid BudgetDTO budgetDTO);

    List<BudgetDTO> createAllBudgets(Long uid, @Valid List<BudgetDTO> budgetDTOList);

    String deleteBudgetOfUser(Long uid, Long bid);

    List<BudgetDTO> getAllBudgetsOfUser(Long uid);

    List<String> validate(Map<String, Object> summaryFilter, BudgetDTO budgetDTOClass);


    List<BudgetSummaryResponse> getBudgetSummary(Long uid, String period, ExpenseCategory expenseCategory);
}
