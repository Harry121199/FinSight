package com.project.ExpenseTracker.payload.expenseGroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class RequestGroupDTO {
    @NotBlank(message = "Group name is mandatory")
    @Size(min = 2, max = 100, message = "Group name must be between 2 and 100 characters")
    private String groupName;

    private List<String> membersIdentifiers;
}
