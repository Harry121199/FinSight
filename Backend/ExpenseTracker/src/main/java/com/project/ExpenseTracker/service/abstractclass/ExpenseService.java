package com.project.ExpenseTracker.service.abstractclass;


import com.project.ExpenseTracker.enums.ExpenseCategory;
import com.project.ExpenseTracker.filter.ExpenseFilterRequest;
import com.project.ExpenseTracker.model.Expense;
import com.project.ExpenseTracker.payload.expense.RequestExpenseDTO;
import com.project.ExpenseTracker.payload.expense.ExpenseSummaryResponse;
import com.project.ExpenseTracker.payload.expense.ExpenseUpdateDTO;
import com.project.ExpenseTracker.payload.expense.ResponseExpenseDTO;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

public interface ExpenseService {
    ResponseExpenseDTO addExpenseOfUser(@Valid RequestExpenseDTO requestExpenseDTO);

    List<String> validation(ExpenseUpdateDTO expenseDTO, Map<String, Object> updates) ;

    ResponseExpenseDTO updateExpenseOfUser(Long eid, Map<String, Object> updates);

    List<String> validateFilter(Expense expense, ExpenseFilterRequest expenseFilterRequest);

    List<ResponseExpenseDTO> getFilterExpenses(ExpenseFilterRequest expenseFilterRequest);

    List<ResponseExpenseDTO> addAllExpenses(@Valid List<RequestExpenseDTO> requestExpenseDTOList);

    List<ExpenseSummaryResponse> getExpenseSummary(ExpenseCategory expenseCategory, String period);
}
