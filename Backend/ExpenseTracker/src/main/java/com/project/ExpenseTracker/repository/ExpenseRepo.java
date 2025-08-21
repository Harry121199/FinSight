package com.project.ExpenseTracker.repository;

import com.project.ExpenseTracker.filter.FilterSpecification;
import com.project.ExpenseTracker.model.Expense;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepo extends JpaRepository<Expense,Long>, JpaSpecificationExecutor<Expense> {
}
