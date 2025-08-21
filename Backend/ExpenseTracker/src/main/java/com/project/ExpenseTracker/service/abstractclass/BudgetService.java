package com.project.ExpenseTracker.service.abstractclass;

import com.project.ExpenseTracker.payload.BudgetDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface BudgetService {
    BudgetDTO createBudget(Long uid, @Valid BudgetDTO budgetDTO);

    List<BudgetDTO> createAllBudgets(Long uid, @Valid List<BudgetDTO> budgetDTOList);

    String deleteBudgetOfUser(Long uid, Long bid);

    List<BudgetDTO> getAllBudgetsOfUser(Long uid);
}
