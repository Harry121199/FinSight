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

    List<ResponseExpenseDTO> getAllExpensesOfUser(Long uid);

    String deleteExpenseOfUser(Long uid, Long eid);

    List<String> validation(Map<String, Object> updates, UserUpdateDTO userDTO);

    ResponseUserDTO updateUserDetails(Long uid, Map<String, Object> updates);

    void deleteUser(Long uid, @Valid UserDeleteRequest userDeleteRequest);
}
