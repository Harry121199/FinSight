package com.project.ExpenseTracker.model;

import com.project.ExpenseTracker.enums.Gender;
import com.project.ExpenseTracker.enums.Roles;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.action.internal.OrphanRemovalAction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    private String firstname;
    private String lastname;


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Roles roles;

    private String password;
    private LocalDate createdOn;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Expense> expenses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Budget> budgets = new ArrayList<>();


    // Helper method to add an expense
    public void addExpense(Expense expense) {
        this.expenses.add(expense);
        expense.setUser(this);
        expense.setTransactionDate(LocalDate.now());
    }

    // Helper method to remove an expense
    public void removeExpense(Expense expense) {
        expenses.remove(expense);
        expense.setUser(null);
    }

    public void addExpense(List<Expense> expenseList) {
        expenseList.forEach(expense -> {
            this.expenses.add(expense);
            expense.setUser(this);
            expense.setTransactionDate(LocalDate.now());
        });
    }

    public void addBudget(Budget budget) {
        this.budgets.add(budget);
        budget.setUser(this);
    }

    public void removeBudget(Budget budget) {
        budgets.remove(budget);
        budget.setUser(null);
    }

    public void addBudget(List<Budget> budgetList) {
        budgetList.forEach(budget -> {
            this.budgets.add(budget);
            budget.setUser(this);
        });
    }
}
