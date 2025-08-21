package com.project.ExpenseTracker.model;

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

}
