package com.project.ExpenseTracker.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseFilterRequest {
    private Map<String, Object> filters;
    private String sortField;
    private String sortDirection;
}
