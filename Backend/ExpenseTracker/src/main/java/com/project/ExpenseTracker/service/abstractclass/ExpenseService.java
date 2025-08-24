package com.project.ExpenseTracker.service.abstractclass;


import com.project.ExpenseTracker.enums.ExpenseCategory;
import com.project.ExpenseTracker.filter.ExpenseFilterRequest;
import com.project.ExpenseTracker.model.Expense;
import com.project.ExpenseTracker.payload.ExpenseDTO;
import com.project.ExpenseTracker.payload.ExpenseSummaryResponse;
import com.project.ExpenseTracker.payload.ExpenseUpdateDTO;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

public interface ExpenseService {
    ExpenseDTO addExpenseOfUser(Long uid, @Valid ExpenseDTO expenseDTO);

    List<String> validation(ExpenseUpdateDTO expenseDTO, Map<String, Object> updates) ;

    ExpenseDTO updateExpenseOfUser(Long uid, Long eid, Map<String, Object> updates);

    List<String> validateFilter(Expense expense, ExpenseFilterRequest expenseFilterRequest);

    List<ExpenseDTO> getFilterExpenses(Long uid, ExpenseFilterRequest expenseFilterRequest);

    List<ExpenseDTO> addAllExpenses(@Valid List<ExpenseDTO> expenseDTOList, Long uid);

    List<ExpenseSummaryResponse> getExpenseSummary(Long uid, ExpenseCategory expenseCategory, String period);
}
