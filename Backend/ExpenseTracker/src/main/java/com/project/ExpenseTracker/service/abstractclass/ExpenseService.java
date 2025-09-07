package com.project.ExpenseTracker.service.abstractclass;


import com.project.ExpenseTracker.enums.ExpenseCategory;
import com.project.ExpenseTracker.filter.ExpenseFilterRequest;
import com.project.ExpenseTracker.model.Expense;
import com.project.ExpenseTracker.payload.dashboard.DashBoardDTO;
import com.project.ExpenseTracker.payload.dashboard.MonthlyTrendDTO;
import com.project.ExpenseTracker.payload.expense.RequestExpenseDTO;
import com.project.ExpenseTracker.payload.expense.ExpenseSummaryResponse;
import com.project.ExpenseTracker.payload.expense.ExpenseUpdateDTO;
import com.project.ExpenseTracker.payload.expense.ResponseExpenseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

import java.io.PrintWriter;
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

    DashBoardDTO gerUserSummary(@Pattern(regexp = "^\\d{4}-\\d{2}$", message = "Period must be in YYYY-MM format") String period);

    List<MonthlyTrendDTO> getMonthlyTrendForYear(Integer month);

    void exportExpenseToCSV(PrintWriter writer);
}
