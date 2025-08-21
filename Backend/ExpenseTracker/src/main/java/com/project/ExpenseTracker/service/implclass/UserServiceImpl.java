package com.project.ExpenseTracker.service.implclass;

import com.project.ExpenseTracker.exception.ExpenseNotFound;
import com.project.ExpenseTracker.exception.UserAlreadyExists;
import com.project.ExpenseTracker.exception.UserNotFound;
import com.project.ExpenseTracker.model.Expense;
import com.project.ExpenseTracker.model.Users;
import com.project.ExpenseTracker.payload.ExpenseDTO;
import com.project.ExpenseTracker.payload.UserDTO;
import com.project.ExpenseTracker.payload.UserUpdateDTO;
import com.project.ExpenseTracker.repository.ExpenseRepo;
import com.project.ExpenseTracker.repository.UserRepo;
import com.project.ExpenseTracker.service.abstractclass.UserService;
import jakarta.validation.Validator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private Validator validator;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ExpenseRepo expenseRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        boolean exist = userRepo.existsByEmail(userDTO.getEmail());
        if(exist) throw new UserAlreadyExists("User already registered with email: " + userDTO.getEmail());
        Users mapped = modelMapper.map(userDTO, Users.class);
        mapped.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        mapped.setCreatedOn(LocalDate.now());
        Users saved = userRepo.save(mapped);
        return modelMapper.map(saved, UserDTO.class);
    }

    @Override
    public UserDTO getAllExpensesOfUser(Long uid) {
        Users user = userRepo.findById(uid)
                .orElseThrow(() -> new UserNotFound("user not found with the given id: " + uid));
        return convertToDto(user);
    }

    @Override
    @Transactional
    public String deleteExpenseOfUser(Long uid, Long eid) {
        Expense expense = expenseRepo.findById(eid)
                .orElseThrow(() -> new ExpenseNotFound("Expense not found with ID: " + eid));
        if(!expense.getUser().getUid().equals(uid)){
            throw new SecurityException("user is authorized to make changes in expense");
        }
        expenseRepo.delete(expense);
        return "Deleted Successfully";
    }

    @Override
    @SuppressWarnings(value = {"unchecked"})
    public List<String> validation(Map<String, Object> updates, UserUpdateDTO userDTO) {
        List<String> errors = new ArrayList<>();
        updates.forEach((key, value) -> {
            try {
                Field field = userDTO.getClass().getDeclaredField(key);
                field.setAccessible(true);
                if (field.getType().isEnum()) {
                    Object enumValue = Enum.valueOf((Class<Enum>) field.getType(), value.toString().toUpperCase());
                    field.set(userDTO, enumValue);
                } else {
                    field.set(userDTO, value);
                }
            } catch (NoSuchFieldException e) {
                errors.add("key with field name ".concat(key).concat(" do not exists."));
            }catch (IllegalAccessException e){
                throw new RuntimeException("something went wrong!!!");
            }
        });
        if (!errors.isEmpty()) {
            return errors;
        }

        // Validate the fully updated object once
        validator.validate(userDTO).forEach(v -> errors.add(v.getMessage()));
        return errors;
    }

    @Override
    @Transactional
    public UserDTO updateUserDetails(Long uid, Map<String, Object> updates) {
        Users users = userRepo.findById(uid)
                .orElseThrow(() -> new UserNotFound("user not found with ID: " + uid));
        mapToObject(updates, users);
        Users saved = userRepo.save(users);
        return modelMapper.map(saved, UserDTO.class);
    }

    @SuppressWarnings(value = {"unchecked"})
    public void mapToObject(Map<String, Object> updates, Users users) {
        updates.forEach((key, value) -> {
            try {
                Field field = users.getClass().getDeclaredField(key);
                field.setAccessible(true);
                if (field.getType().isEnum()) {
                    Object enumValue = Enum.valueOf((Class<Enum>) field.getType(), value.toString().toUpperCase());
                    field.set(users, enumValue);
                } else {
                    field.set(users, value);
                }
            }
            catch (IllegalAccessException | NoSuchFieldException e){
                throw new RuntimeException("something went wrong!!!");
            }
        });
    }

    public UserDTO convertToDto(Users user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        List<ExpenseDTO> expenseDTOS = user.getExpenses()
                .stream()
                .map(expense -> modelMapper.map(expense, ExpenseDTO.class))
                .collect(Collectors.toList());
        userDTO.setExpenses(expenseDTOS);
        return userDTO;
    }
}
