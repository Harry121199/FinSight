package com.project.ExpenseTracker.service.implclass;


import com.project.ExpenseTracker.enums.ExpenseCategory;
import com.project.ExpenseTracker.exception.ExpenseNotFound;
import com.project.ExpenseTracker.exception.UserNotFound;
import com.project.ExpenseTracker.filter.ExpenseFilterRequest;
import com.project.ExpenseTracker.filter.ExpenseFilterSpecification;
import com.project.ExpenseTracker.model.Expense;
import com.project.ExpenseTracker.model.Users;
import com.project.ExpenseTracker.payload.expense.RequestExpenseDTO;
import com.project.ExpenseTracker.payload.expense.ExpenseSummaryResponse;
import com.project.ExpenseTracker.payload.expense.ExpenseUpdateDTO;
import com.project.ExpenseTracker.payload.expense.ResponseExpenseDTO;
import com.project.ExpenseTracker.repository.ExpenseRepo;
import com.project.ExpenseTracker.repository.UserRepo;
import com.project.ExpenseTracker.service.abstractclass.ExpenseService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.Validator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    @Autowired
    private Validator validator;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ExpenseRepo expenseRepo;
    @Autowired
    private EntityManager entityManager;


    @Override
    @Transactional
    public ResponseExpenseDTO addExpenseOfUser(Long uid, RequestExpenseDTO requestExpenseDTO) {
        Users users = userRepo.findById(uid).orElseThrow(() -> new UserNotFound("User doesn't exists"));
        Expense expense = modelMapper.map(requestExpenseDTO, Expense.class);
        expense.setUser(users);
        Expense saved = expenseRepo.save(expense);
        return modelMapper.map(saved, ResponseExpenseDTO.class);
    }

    @Override
    @Transactional
    public List<ResponseExpenseDTO> addAllExpenses(List<RequestExpenseDTO> requestExpenseDTOList, Long uid) {
        Users users = userRepo.findById(uid)
                .orElseThrow(() -> new UserNotFound("User cannot be found with ID: " + uid));
        List<Expense> expenseList = requestExpenseDTOList.stream()
                .map(expenseDTO -> modelMapper.map(expenseDTO, Expense.class))
                .toList();
        return expenseList.stream()
                .map(expense -> {
                    expense.setUser(users);
                    Expense saved = expenseRepo.save(expense);
                    return modelMapper.map(saved, ResponseExpenseDTO.class);
                }).toList();
    }

    @SuppressWarnings(value = {"unchecked"})
    private void mapToObject(Expense expense, Map<String, Object> updates) {
        updates.forEach((key, value) -> {
            try {
                Field field = expense.getClass().getDeclaredField(key);
                field.setAccessible(true);
                if (field.getType().isEnum()) {
                    // Convert the String to the correct Enum constant
                    Object enumValue = Enum.valueOf((Class<Enum>) field.getType(), value.toString().toUpperCase());
                    field.set(expense, enumValue);
                } else {
                    // For all other fields, set the value directly
                    field.set(expense, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    @SuppressWarnings(value = {"unchecked"})
    public List<String> validation(ExpenseUpdateDTO expenseDTO, Map<String, Object> updates) {
        List<String> errors = new ArrayList<>();
        String key = "";
        Object value;
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            try {
            key = entry.getKey();
            value = entry.getValue();
            Field field = expenseDTO.getClass().getDeclaredField(key);
            field.setAccessible(true);
                if (field.getType().isEnum()) {
                    // Convert the String to the correct Enum constant
                    Object enumValue = Enum.valueOf((Class<Enum>) field.getType(), value.toString().toUpperCase());
                    field.set(expenseDTO, enumValue);
                } else {
                    // For all other fields, set the value directly
                    field.set(expenseDTO, value);
                }
            }catch (NoSuchFieldException e){
                errors.add("Key with filed name ".concat(key).concat(" doesn't exists"));
            }catch (IllegalAccessException e) {
                throw new RuntimeException("something went wrong. Try reloading page again.");
            }
        }
        if(!errors.isEmpty()) return errors;
        validator.validate(expenseDTO).stream()
                .map(expenseUpdateDTOConstraintViolation -> expenseUpdateDTOConstraintViolation.getMessage())
                .forEach(e -> errors.add(e));
        return errors;
    }

    @Override
    @Transactional
    public ResponseExpenseDTO updateExpenseOfUser(Long uid, Long eid, Map<String, Object> updates) {

        Expense expense = expenseRepo.findById(eid)
                .orElseThrow(() -> new ExpenseNotFound("No expense found with ID: " + eid));
        if (!expense.getUser().getUid().equals(uid)) {
            throw new SecurityException("user is not authorized to make changes in expense");
        }
        mapToObject(expense, updates);
        Expense saved = expenseRepo.save(expense);
        return modelMapper.map(saved, ResponseExpenseDTO.class);
    }

    @Override
    public List<String> validateFilter(Expense expense, ExpenseFilterRequest expenseFilterRequest) {
        List<String> errors = new ArrayList<>(
                expenseFilterRequest.getFilters().keySet().stream()
                .map(key -> {
                    try {
                        Field field = expense.getClass().getDeclaredField(key);
                    } catch (NoSuchFieldException e) {
                        return "No field exists with key: ".concat(key);
                    }
                    return null;
                }).filter(Objects::nonNull)
                .toList()
        );
        try {
            Field field = expense.getClass().getDeclaredField(expenseFilterRequest.getSortField());
        } catch (NoSuchFieldException e) {
            errors.add("Invalid sort field");
        }
        return errors;
    }

    @Override
    public List<ResponseExpenseDTO> getFilterExpenses(Long uid, ExpenseFilterRequest expenseFilterRequest) {
        ExpenseFilterSpecification<Expense> expenseFilterSpecification = new ExpenseFilterSpecification<>(uid, expenseFilterRequest.getFilters());
        Sort sort = Sort.unsorted();
        if(expenseFilterRequest.getSortField()!=null&&!expenseFilterRequest.getSortField().isEmpty()){
            Sort.Direction direction = "desc".equalsIgnoreCase(expenseFilterRequest.getSortDirection())
                    ? Sort.Direction.DESC: Sort.Direction.ASC;
            sort = Sort.by(direction, expenseFilterRequest.getSortField());
        }
        return expenseRepo.findAll(expenseFilterSpecification, sort).stream()
                .map(expense -> modelMapper.map(expense, ResponseExpenseDTO.class))
                .toList();
    }

    @Override
    public List<ExpenseSummaryResponse> getExpenseSummary(Long uid, ExpenseCategory expenseCategory, String period) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ExpenseSummaryResponse> cq = cb.createQuery(ExpenseSummaryResponse.class);
        Root<Expense> expenseRoot = cq.from(Expense.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(expenseRoot.get("user").get("uid"), uid));
        if(period!=null&&!period.isEmpty()){
            YearMonth yearMonth = YearMonth.parse(period);
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();
            predicates.add(cb.between(expenseRoot.get("transactionDate"), startDate, endDate));
        }
        if (expenseCategory!=null) {
            predicates.add(cb.equal(expenseRoot.get("expenseCategory"), expenseCategory));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        cq.groupBy(expenseRoot.get("expenseCategory"));

        cq.select(cb.construct(
                ExpenseSummaryResponse.class,
                expenseRoot.get("expenseCategory"),
                cb.sum(expenseRoot.get("amount"))
        ));
        return entityManager.createQuery(cq).getResultList();
    }
}
