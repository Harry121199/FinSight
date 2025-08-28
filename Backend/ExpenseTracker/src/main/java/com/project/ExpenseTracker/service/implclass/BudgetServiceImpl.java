package com.project.ExpenseTracker.service.implclass;

import com.project.ExpenseTracker.enums.ExpenseCategory;
import com.project.ExpenseTracker.exception.BudgetAlreadyExists;
import com.project.ExpenseTracker.exception.BudgetNotFound;
import com.project.ExpenseTracker.exception.UserNotFound;
import com.project.ExpenseTracker.model.Budget;
import com.project.ExpenseTracker.model.Expense;
import com.project.ExpenseTracker.model.Users;
import com.project.ExpenseTracker.payload.budget.RequestBudgetDTO;
import com.project.ExpenseTracker.payload.budget.ResponseBudgetDTO;
import com.project.ExpenseTracker.payload.budget.BudgetSummaryResponse;
import com.project.ExpenseTracker.repository.BudgetRepo;
import com.project.ExpenseTracker.repository.UserRepo;
import com.project.ExpenseTracker.service.abstractclass.BudgetService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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


    private Users getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFound("User not loggedIn"));
    }


    @Override
    @Transactional
    public ResponseBudgetDTO createBudget(@Valid RequestBudgetDTO requestBudgetDTO) {
        Users currentUser = getCurrentUser();
        boolean doBudgetExists = budgetRepo.existsByUserAndExpenseCategoryAndPeriod(currentUser, requestBudgetDTO.getExpenseCategory(), requestBudgetDTO.getPeriod());
        if (doBudgetExists) {
            throw new BudgetAlreadyExists("Budget already exists with category: " + requestBudgetDTO.getExpenseCategory());
        }

        Budget mapped = modelMapper.map(requestBudgetDTO, Budget.class);
        mapped.setUser(currentUser);
        Budget saved = budgetRepo.save(mapped);
        return modelMapper.map(saved, ResponseBudgetDTO.class);
    }

    @Override
    @Transactional
    public List<ResponseBudgetDTO> createAllBudgets(@Valid List<RequestBudgetDTO> requestBudgetDTOList) {
        Users currentUser = getCurrentUser();
        return requestBudgetDTOList.stream().map(responseBudgetDTO -> {
            if (budgetRepo.existsByUserAndExpenseCategoryAndPeriod(currentUser, responseBudgetDTO.getExpenseCategory(), responseBudgetDTO.getPeriod())) {
                throw new BudgetAlreadyExists("Budget already exists with category: " + responseBudgetDTO.getExpenseCategory());
            }
            Budget mapped = modelMapper.map(responseBudgetDTO, Budget.class);
            mapped.setUser(currentUser);
            Budget saved = budgetRepo.save(mapped);
            return modelMapper.map(saved, ResponseBudgetDTO.class);
        }).toList();
    }

    @Override
    @Transactional
    public String deleteBudgetOfUser(Long bid) {
        Users currentUser = getCurrentUser();
        Budget budget = budgetRepo.findById(bid)
                .orElseThrow(() -> new BudgetNotFound("Budget not found with ID: " + bid));
        if (!budget.getUser().getUid().equals(currentUser.getUid())) {
            throw new SecurityException("User not allowed to make changes in the budget");
        }
        budgetRepo.delete(budget);
        return "Successfully Deleted";
    }

    @Override
    public List<ResponseBudgetDTO> getAllBudgetsOfUser() {
        Users currentUser = getCurrentUser();
        return currentUser.getBudgets().stream()
                .map(budget -> modelMapper.map(budget, ResponseBudgetDTO.class))
                .toList();
    }

    @Override
    public List<String> validate(Map<String, Object> summaryFilter, RequestBudgetDTO requestBudgetDTO) {
        List<String> errors = new ArrayList<>();
        summaryFilter
                .forEach((key,value)->{
                    try {
                        Field field = requestBudgetDTO.getClass().getDeclaredField(key);
                        field.setAccessible(true);
                        field.set(requestBudgetDTO, value);
                    } catch (NoSuchFieldException e) {
                        errors.add("Field cannot be found with name: ".concat(key));
                    } catch (IllegalAccessException e) {
                        throw new BudgetAlreadyExists("something went wrong");
                    }
                });
        if(!errors.isEmpty()) return errors;
        validator.validate(requestBudgetDTO).forEach(budgetDTOConstraintViolation -> errors.add(budgetDTOConstraintViolation.getMessage()));
        return errors;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetSummaryResponse> getBudgetSummary(String period, ExpenseCategory expenseCategory) {
        Users currentUser = getCurrentUser();
        List<Budget> budgetsToProcess = new ArrayList<>();

        if(expenseCategory!=null){
            budgetRepo.findByUserUidAndPeriodAndExpenseCategory(currentUser.getUid(), period, expenseCategory)
                    .ifPresent(budgetsToProcess::add);
        }else {
            budgetsToProcess = budgetRepo.findByUserUidAndPeriod(currentUser.getUid(), period);
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

                    Predicate userPredicate = cb.equal(expenseRoot.get("user").get("uid"), currentUser.getUid());
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
