package com.project.ExpenseTracker.service.abstractclass;

import com.project.ExpenseTracker.payload.expense.ResponseExpenseDTO;
import com.project.ExpenseTracker.payload.user.RequestUserDTO;
import com.project.ExpenseTracker.payload.user.ResponseUserDTO;
import com.project.ExpenseTracker.payload.user.UserDeleteRequest;
import com.project.ExpenseTracker.payload.user.UserUpdateDTO;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

public interface UserService {
    ResponseUserDTO createUser(@Valid RequestUserDTO userDTO);

    List<ResponseExpenseDTO> getAllExpensesOfUser();

    String deleteExpenseOfUser(Long eid);

    List<String> validation(Map<String, Object> updates, UserUpdateDTO userDTO);

    ResponseUserDTO updateUserDetails(Map<String, Object> updates);

    String deleteUser(@Valid UserDeleteRequest userDeleteRequest);
}
