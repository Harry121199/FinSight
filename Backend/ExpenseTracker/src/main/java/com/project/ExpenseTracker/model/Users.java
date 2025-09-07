package com.project.ExpenseTracker.model;

import com.project.ExpenseTracker.enums.Gender;
import com.project.ExpenseTracker.enums.Roles;
import jakarta.persistence.*;
import lombok.*;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Expense> expenses = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Budget> budgets = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ExpenseGroup> createdExpenseGroups = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<RecurringExpense> recurringExpenses = new ArrayList<>();


    @ManyToMany(mappedBy = "members", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ExpenseGroup> expenseGroups = new ArrayList<>();

}
