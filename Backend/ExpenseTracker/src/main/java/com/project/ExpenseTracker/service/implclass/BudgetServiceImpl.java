package com.project.ExpenseTracker.service.implclass;

import com.project.ExpenseTracker.enums.ExpenseCategory;
import com.project.ExpenseTracker.exception.BudgetAlreadyExists;
import com.project.ExpenseTracker.exception.BudgetNotFound;
import com.project.ExpenseTracker.exception.UserNotFound;
import com.project.ExpenseTracker.model.Budget;
import com.project.ExpenseTracker.model.Expense;
import com.project.ExpenseTracker.model.Users;
import com.project.ExpenseTracker.payload.BudgetDTO;
import com.project.ExpenseTracker.payload.BudgetSummaryResponse;
import com.project.ExpenseTracker.repository.BudgetRepo;
import com.project.ExpenseTracker.repository.UserRepo;
import com.project.ExpenseTracker.service.abstractclass.BudgetService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.Validator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private Validator validator;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BudgetRepo budgetRepo;
    @Autowired
    private EntityManager entityManager;



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

    @Override
    public List<String> validate(Map<String, Object> summaryFilter, BudgetDTO budgetDTOClass) {
        List<String> errors = new ArrayList<>();
        summaryFilter
                .forEach((key,value)->{
                    try {
                        Field field = budgetDTOClass.getClass().getDeclaredField(key);
                        field.setAccessible(true);
                        field.set(budgetDTOClass, value);
                    } catch (NoSuchFieldException e) {
                        errors.add("Field cannot be found with name: ".concat(key));
                    } catch (IllegalAccessException e) {
                        throw new BudgetAlreadyExists("something went wrong");
                    }
                });
        if(!errors.isEmpty()) return errors;
        validator.validate(budgetDTOClass).forEach(budgetDTOConstraintViolation -> errors.add(budgetDTOConstraintViolation.getMessage()));
        return errors;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetSummaryResponse> getBudgetSummary(Long uid, String period, ExpenseCategory expenseCategory) {
        List<Budget> budgetsToProcess = new ArrayList<>();
        if(expenseCategory!=null){
            budgetRepo.findByUserUidAndPeriodAndExpenseCategory(uid, period, expenseCategory)
                    .ifPresent(budgetsToProcess::add);
        }else {
            budgetsToProcess = budgetRepo.findByUserUidAndPeriod(uid, period);
        }

        List<BudgetSummaryResponse> summaries = new ArrayList<>();
        YearMonth yearMonth = YearMonth.parse(period);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return budgetsToProcess.stream()
                .map(budget -> {
                    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                    CriteriaQuery<Double> cq = cb.createQuery(Double.class);
                    Root<Expense> expenseRoot = cq.from(Expense.class);

                    Predicate userPredicate = cb.equal(expenseRoot.get("user").get("uid"), uid);
                    Predicate categoryPredicate = cb.equal(expenseRoot.get("expenseCategory"), budget.getExpenseCategory());
                    Predicate datePredicate = cb.between(expenseRoot.get("transactionDate"), startDate, endDate);
                    cq.where(cb.and((userPredicate), categoryPredicate, datePredicate));

                    cq.select(cb.sum(expenseRoot.get("amount")));

                    Double spentAmount = entityManager.createQuery(cq).getSingleResult();

                    spentAmount = spentAmount == null ? 0.0 : spentAmount;
                    return new BudgetSummaryResponse(budget.getExpenseCategory(), budget.getAmount(), spentAmount, budget.getAmount() - spentAmount);
                }).collect(Collectors.toList());

    }
}
