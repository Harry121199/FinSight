package com.project.ExpenseTracker.model;

import com.project.ExpenseTracker.enums.ExpenseCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseCategory expenseCategory;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    @ToString.Exclude
    private Users user;

}
