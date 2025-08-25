package com.project.ExpenseTracker.service.abstractclass;

import com.project.ExpenseTracker.enums.ExpenseCategory;
import com.project.ExpenseTracker.payload.budget.RequestBudgetDTO;
import com.project.ExpenseTracker.payload.budget.ResponseBudgetDTO;
import com.project.ExpenseTracker.payload.budget.BudgetSummaryResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

public interface BudgetService {
    ResponseBudgetDTO createBudget(Long uid, @Valid RequestBudgetDTO requestBudgetDTO);

    List<ResponseBudgetDTO> createAllBudgets(Long uid, @Valid List<RequestBudgetDTO> requestBudgetDTOList);

    String deleteBudgetOfUser(Long uid, Long bid);

    List<ResponseBudgetDTO> getAllBudgetsOfUser(Long uid);

    List<String> validate(Map<String, Object> summaryFilter, RequestBudgetDTO requestBudgetDTO);


    List<BudgetSummaryResponse> getBudgetSummary(Long uid, String period, ExpenseCategory expenseCategory);
}
