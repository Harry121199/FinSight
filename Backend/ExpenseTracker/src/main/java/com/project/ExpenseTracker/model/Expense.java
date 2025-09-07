package com.project.ExpenseTracker.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.project.ExpenseTracker.enums.ExpenseCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eid;

    @Column(nullable = false)
    private String itemName;

    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExpenseCategory expenseCategory;

    @Column(nullable = false)
    private Double amount;

    private LocalDate transactionDate = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY) // Lazy to avoid unnecessary data loading
    @JoinColumn(name = "uid")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gid")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ExpenseGroup expenseGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rid")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private RecurringExpense recurringExpense;
}
