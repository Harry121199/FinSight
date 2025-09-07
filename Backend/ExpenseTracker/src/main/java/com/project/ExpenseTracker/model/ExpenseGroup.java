package com.project.ExpenseTracker.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class  ExpenseGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gid;

    @Column(nullable = false)
    private String groupName;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy to avoid unnecessary data loading
    @JoinColumn(name = "uid")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Users createdBy;


    @ManyToMany
    @JoinTable(
            name = "group_members",
            joinColumns = @JoinColumn(name = "gid"),
            inverseJoinColumns = @JoinColumn(name = "uid")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Users> members = new ArrayList<>();

    @OneToMany(mappedBy = "expenseGroup", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Expense> expenses = new ArrayList<>();
}
