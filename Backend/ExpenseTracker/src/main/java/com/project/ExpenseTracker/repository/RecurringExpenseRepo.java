package com.project.ExpenseTracker.repository;

import com.project.ExpenseTracker.model.RecurringExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecurringExpenseRepo extends JpaRepository<RecurringExpense, Long> {

    List<RecurringExpense> findAllByDayOfMonthAndStartDateLessThanEqual(Integer currentDay, LocalDate today);

    List<RecurringExpense> findAllByUserUid(Long uid);
}
