package com.project.ExpenseTracker.service.abstractclass;

import com.project.ExpenseTracker.payload.UserDTO;
import com.project.ExpenseTracker.payload.UserDeleteRequest;
import com.project.ExpenseTracker.payload.UserUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

public interface UserService {
    UserDTO createUser(@Valid UserDTO userDTO);

    UserDTO getAllExpensesOfUser(Long uid);

    String deleteExpenseOfUser(Long uid, Long eid);

    List<String> validation(Map<String, Object> updates, UserUpdateDTO userDTO);

    UserDTO updateUserDetails(Long uid, Map<String, Object> updates);

    void deleteUser(Long uid, @Valid UserDeleteRequest userDeleteRequest);
}
