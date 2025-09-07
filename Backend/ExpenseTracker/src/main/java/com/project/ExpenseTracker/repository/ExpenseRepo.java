package com.project.ExpenseTracker.repository;

import com.project.ExpenseTracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepo extends JpaRepository<Expense,Long>, JpaSpecificationExecutor<Expense> {

    @Query("SELECT COUNT(e) > 0 FROM Expense e WHERE e.recurringExpense.rid = :rid AND YEAR(e.transactionDate) = :year AND MONTH(e.transactionDate) = :month")
    boolean existsByRecurringSourceForMonth(@Param("rid") Long recurringId, @Param("year") int year, @Param("month") int month);

    List<Expense> findAllByUserUid(Long uid);
}
