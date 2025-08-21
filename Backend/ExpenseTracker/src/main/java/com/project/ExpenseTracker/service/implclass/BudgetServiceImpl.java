package com.project.ExpenseTracker.service.implclass;

import com.project.ExpenseTracker.exception.BudgetAlreadyExists;
import com.project.ExpenseTracker.exception.BudgetNotFound;
import com.project.ExpenseTracker.exception.UserNotFound;
import com.project.ExpenseTracker.model.Budget;
import com.project.ExpenseTracker.model.Users;
import com.project.ExpenseTracker.payload.BudgetDTO;
import com.project.ExpenseTracker.repository.BudgetRepo;
import com.project.ExpenseTracker.repository.UserRepo;
import com.project.ExpenseTracker.service.abstractclass.BudgetService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BudgetRepo budgetRepo;


    @Override
    @Transactional
    public BudgetDTO createBudget(Long uid, BudgetDTO budgetDTO) {
        Users users = userRepo.findById(uid)
                .orElseThrow(() -> new UserNotFound("User not exist with ID: " + uid));
        boolean doBudgetExists = budgetRepo.existsByUserAndExpenseCategoryAndPeriod(users, budgetDTO.getExpenseCategory(),budgetDTO.getPeriod());
        if (doBudgetExists) {
            throw new BudgetAlreadyExists("Budget already exists with category: " + budgetDTO.getExpenseCategory());
        }

        Budget mapped = modelMapper.map(budgetDTO, Budget.class);

        users.addBudget(mapped);
        Budget saved = budgetRepo.save(mapped);
        return modelMapper.map(saved, BudgetDTO.class);
    }

    @Override
    @Transactional
    public List<BudgetDTO> createAllBudgets(Long uid, List<BudgetDTO> budgetDTOList) {

        Users users = userRepo.findById(uid)
                .orElseThrow(() -> new UserNotFound("User not found with ID: " + uid));
        return budgetDTOList.stream().map(budgetDTO -> {
            if (budgetRepo.existsByUserAndExpenseCategoryAndPeriod(users, budgetDTO.getExpenseCategory(),budgetDTO.getPeriod())) {
                throw new BudgetAlreadyExists("Budget already exists with category: " + budgetDTO.getExpenseCategory());
            }
            Budget mapped = modelMapper.map(budgetDTO, Budget.class);
            users.addBudget(mapped);
            Budget saved = budgetRepo.save(mapped);
            return modelMapper.map(saved, BudgetDTO.class);
        }).toList();
    }

    @Override
    @Transactional
    public String deleteBudgetOfUser(Long uid, Long bid) {
        Budget budget = budgetRepo.findById(bid)
                .orElseThrow(() -> new BudgetNotFound("Budget not found with ID: " + bid));
        if (!budget.getUser().getUid().equals(uid)) {
            throw new SecurityException("User not allowed to make changes in the budget");
        }
        budgetRepo.delete(budget);
        return "Successfully Deleted";
    }

    @Override
    public List<BudgetDTO> getAllBudgetsOfUser(Long uid) {
        Users users = userRepo.findById(uid)
                .orElseThrow(() -> new UserNotFound("User not found with ID: " + uid));
        return users.getBudgets().stream()
                .map(budget -> modelMapper.map(budget, BudgetDTO.class))
                .toList();
    }
}
