package com.project.ExpenseTracker.service.abstractclass;

import com.project.ExpenseTracker.payload.recurringExpenses.RequestRecurringExpenseDTO;
import com.project.ExpenseTracker.payload.recurringExpenses.ResponseRecurringExpenseDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface RecurringExpenseService {
    ResponseRecurringExpenseDTO createRecurringExpense(@Valid RequestRecurringExpenseDTO requestRecurringExpenseDTO);

    List<ResponseRecurringExpenseDTO> createAllRecurringExpenses(@Valid List<RequestRecurringExpenseDTO> recurringExpenseDTOS);

    List<ResponseRecurringExpenseDTO> getAllRecurringService();

    String deleteRecurringExpenseById(Long rid);
}
