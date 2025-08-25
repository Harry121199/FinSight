package com.project.ExpenseTracker.service.abstractclass;

import com.project.ExpenseTracker.payload.expenseGroup.RequestGroupDTO;
import com.project.ExpenseTracker.payload.expenseGroup.ResponseGroupDTO;
import jakarta.validation.Valid;

public interface ExpenseGroupService {
    ResponseGroupDTO createGroup(Long uid, @Valid RequestGroupDTO requestGroupDTO);
}
