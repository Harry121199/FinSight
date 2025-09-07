package com.project.ExpenseTracker.repository;

import com.project.ExpenseTracker.enums.ExpenseCategory;
import com.project.ExpenseTracker.model.Budget;
import com.project.ExpenseTracker.model.Users;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepo extends JpaRepository<Budget, Long>, JpaSpecificationExecutor<Budget> {

    boolean existsByUserAndExpenseCategoryAndPeriod(Users users, @Nullable ExpenseCategory expenseCategory, @NotBlank(message = "Period is mandatory") @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "Period must be in YYYY-MM format") String period);

    List<Budget> findByUserUidAndPeriod(Long uid, String period);

    Optional<Budget> findByUserUidAndPeriodAndExpenseCategory(Long uid, String period, ExpenseCategory expenseCategory);
}
