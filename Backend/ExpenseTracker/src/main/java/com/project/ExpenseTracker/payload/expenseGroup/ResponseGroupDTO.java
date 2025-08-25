package com.project.ExpenseTracker.payload.expenseGroup;

import com.project.ExpenseTracker.payload.expense.ResponseExpenseDTO;
import com.project.ExpenseTracker.payload.user.ResponseUserDTO;
import lombok.Data;

import java.util.List;

@Data
public class ResponseGroupDTO {
    private Long gid;
    private String groupName;
    private ResponseUserDTO createdBy;
    private List<ResponseUserDTO> members;
    private List<ResponseExpenseDTO> expenses;
}
