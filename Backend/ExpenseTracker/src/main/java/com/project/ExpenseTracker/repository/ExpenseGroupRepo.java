package com.project.ExpenseTracker.repository;

import com.project.ExpenseTracker.model.ExpenseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseGroupRepo extends JpaRepository<ExpenseGroup,Long>, JpaSpecificationExecutor<ExpenseGroup> {
}
