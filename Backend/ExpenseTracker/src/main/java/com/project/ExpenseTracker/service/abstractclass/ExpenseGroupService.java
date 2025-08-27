package com.project.ExpenseTracker.service.abstractclass;

import com.project.ExpenseTracker.payload.expense.RequestExpenseDTO;
import com.project.ExpenseTracker.payload.expense.ResponseExpenseDTO;
import com.project.ExpenseTracker.payload.expenseGroup.RequestGroupDTO;
import com.project.ExpenseTracker.payload.expenseGroup.ResponseGroupDTO;
import com.project.ExpenseTracker.payload.user.ResponseUserDTO;
import com.project.ExpenseTracker.payload.user.UserNameDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface ExpenseGroupService {
    ResponseGroupDTO createGroup(Long uid, @Valid RequestGroupDTO requestGroupDTO);

    List<ResponseExpenseDTO> addAllExpenses(Long gid, @Valid List<RequestExpenseDTO> requestExpenseDTOList);

    ResponseExpenseDTO addExpense(Long gid, @Valid RequestExpenseDTO requestExpenseDTO);

    String addUser(@Valid UserNameDTO userNameDTO, Long gid, Long uid);

    List<ResponseUserDTO> getAllUser(Long gid, Long uid);
}
