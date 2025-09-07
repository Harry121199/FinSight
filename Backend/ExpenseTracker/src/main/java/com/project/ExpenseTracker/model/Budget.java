package com.project.ExpenseTracker.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.project.ExpenseTracker.enums.ExpenseCategory;
import jakarta.persistence.*;
import lombok.*;

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
    @EqualsAndHashCode.Exclude
    private Users user;
}
