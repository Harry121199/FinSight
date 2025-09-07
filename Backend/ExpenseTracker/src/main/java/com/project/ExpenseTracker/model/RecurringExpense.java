package com.project.ExpenseTracker.model;


import com.project.ExpenseTracker.enums.ExpenseCategory;
import com.project.ExpenseTracker.enums.Frequency;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecurringExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rid;

    @Column(nullable = false)
    private String itemName;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseCategory expenseCategory;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Frequency frequency;

    @Column(nullable = false)
    private Integer dayOfMonth;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    private Users user;

    @OneToMany(mappedBy = "recurringExpense", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Expense> generatedExpenses = new ArrayList<>();
}
