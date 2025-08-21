package com.project.ExpenseTracker.service.implclass;


import com.project.ExpenseTracker.exception.ExpenseNotFound;
import com.project.ExpenseTracker.exception.UserNotFound;
import com.project.ExpenseTracker.filter.FilterRequest;
import com.project.ExpenseTracker.filter.FilterSpecification;
import com.project.ExpenseTracker.model.Expense;
import com.project.ExpenseTracker.model.Users;
import com.project.ExpenseTracker.payload.ExpenseDTO;
import com.project.ExpenseTracker.payload.ExpenseUpdateDTO;
import com.project.ExpenseTracker.repository.ExpenseRepo;
import com.project.ExpenseTracker.repository.UserRepo;
import com.project.ExpenseTracker.service.abstractclass.ExpenseService;
import jakarta.validation.Validator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDate;
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

    @Override
    @Transactional
    public ExpenseDTO addExpenseOfUser(Long uid, ExpenseDTO expenseDTO) {
        Users users = userRepo.findById(uid).orElseThrow(() -> new UserNotFound("User doesn't exists"));
        Expense expense = modelMapper.map(expenseDTO, Expense.class);
        users.addExpense(expense);
        userRepo.save(users);
        return modelMapper.map(expense, ExpenseDTO.class);
    }

    @Override
    @Transactional
    public List<ExpenseDTO> addAllExpenses(List<ExpenseDTO> expenseDTOList, Long uid) {
        Users users = userRepo.findById(uid)
                .orElseThrow(() -> new UserNotFound("User cannot be found with ID: " + uid));
        List<Expense> expenseList = expenseDTOList.stream()
                .map(expenseDTO -> modelMapper.map(expenseDTO, Expense.class))
                .toList();
        users.addExpense(expenseList);
        userRepo.save(users);

        return expenseList.stream()
                .map(expense -> modelMapper.map(expense, ExpenseDTO.class))
                .toList();
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
    public ExpenseDTO updateExpenseOfUser(Long uid, Long eid, Map<String, Object> updates) {

        Expense expense = expenseRepo.findById(eid)
                .orElseThrow(() -> new ExpenseNotFound("No expense found with ID: " + eid));
        if (!expense.getUser().getUid().equals(uid)) {
            throw new SecurityException("user is not authorized to make changes in expense");
        }
        mapToObject(expense, updates);
        Expense saved = expenseRepo.save(expense);
        return modelMapper.map(saved, ExpenseDTO.class);
    }

    @Override
    public List<String> validateFilter(Expense expense, FilterRequest filterRequest) {
        List<String> errors = new ArrayList<>(
                filterRequest.getFilters().keySet().stream()
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
            Field field = expense.getClass().getDeclaredField(filterRequest.getSortField());
        } catch (NoSuchFieldException e) {
            errors.add("Invalid sort field");
        }
        return errors;
    }

    @Override
    public List<ExpenseDTO> getFilterExpenses(Long uid, FilterRequest filterRequest) {
        FilterSpecification<Expense> filterSpecification = new FilterSpecification<>(uid, filterRequest.getFilters());
        Sort sort = Sort.unsorted();
        if(filterRequest.getSortField()!=null&&!filterRequest.getSortField().isEmpty()){
            Sort.Direction direction = "desc".equalsIgnoreCase(filterRequest.getSortDirection())
                    ? Sort.Direction.DESC: Sort.Direction.ASC;
            sort = Sort.by(direction, filterRequest.getSortField());
        }
        return expenseRepo.findAll(filterSpecification, sort).stream()
                .map(expense -> modelMapper.map(expense, ExpenseDTO.class))
                .toList();
    }
}
