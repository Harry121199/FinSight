package com.project.ExpenseTracker.payload.expense;

import com.project.ExpenseTracker.enums.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RequestExpenseDTO {
    @NotBlank(message = "Item name is mandatory")
    @Size(min = 2, max = 100, message = "Item name must be between 2 and 100 characters")
    private String itemName;

    @Size(max = 255, message = "Description cannot be longer than 255 characters")
    private String description;

    @NotNull(message = "Category is mandatory")
    private ExpenseCategory expenseCategory;


    @NotNull(message = "Amount is mandatory")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private Double amount;

    private LocalDate transactionDate;
}
