package com.project.ExpenseTracker.service.implclass;

import com.project.ExpenseTracker.exception.ExpenseNotFound;
import com.project.ExpenseTracker.exception.UserAlreadyExists;
import com.project.ExpenseTracker.model.Expense;
import com.project.ExpenseTracker.model.Users;
import com.project.ExpenseTracker.payload.expense.ResponseExpenseDTO;
import com.project.ExpenseTracker.payload.user.RequestUserDTO;
import com.project.ExpenseTracker.payload.user.ResponseUserDTO;
import com.project.ExpenseTracker.payload.user.UserDeleteRequest;
import com.project.ExpenseTracker.payload.user.UserUpdateDTO;
import com.project.ExpenseTracker.repository.ExpenseRepo;
import com.project.ExpenseTracker.repository.UserRepo;
import com.project.ExpenseTracker.service.abstractclass.UserService;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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

    @Autowired
    private AuthenticationManager authenticationManager;


    private Users getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new SecurityException("User not loggedIn with ID:".concat(userDetails.getUsername())));
    }

    @Override
    @Transactional
    public ResponseUserDTO createUser(@Valid RequestUserDTO userDTO) {
        boolean exist = userRepo.existsByEmail(userDTO.getEmail());
        if(exist) throw new UserAlreadyExists("User already registered with email: " + userDTO.getEmail());
        Users mapped = modelMapper.map(userDTO, Users.class);
        mapped.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        mapped.setCreatedOn(LocalDate.now());
        Users saved = userRepo.save(mapped);
        return modelMapper.map(saved, ResponseUserDTO.class);
    }

    @Override
    public List<ResponseExpenseDTO> getAllExpensesOfUser() {
        Users currentUser = getCurrentUser();
        return currentUser.getExpenses().stream()
                .map(expense -> modelMapper.map(expense, ResponseExpenseDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public String deleteExpenseOfUser(Long eid) {
        Users currentUser = getCurrentUser();
        Expense expense = expenseRepo.findById(eid)
                .orElseThrow(() -> new ExpenseNotFound("Expense not found with ID: " + eid));
        if (!expense.getUser().getUid().equals(currentUser.getUid())) {
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
                } else if (field.getName().equals("email")) {
                    throw new SecurityException("Email cannot be updated");
                } else {
                    field.set(userDTO, value);
                }
            } catch (NoSuchFieldException e) {
                errors.add("key with field name ".concat(key).concat(" do not exists."));
            } catch (SecurityException e) {
                errors.add(e.getMessage());
            } catch (IllegalAccessException e) {
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
    public ResponseUserDTO updateUserDetails(Map<String, Object> updates) {
        Users currentUser = getCurrentUser();
        mapToObject(updates, currentUser);
        Users saved = userRepo.save(currentUser);
        return modelMapper.map(saved, ResponseUserDTO.class);
    }

    @Override
    @Transactional
    public String deleteUser(UserDeleteRequest userDeleteRequest) {
        Users currentUser = getCurrentUser();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            currentUser.getEmail(),
                            userDeleteRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new SecurityException("You are not authorized to delete this account");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        userRepo.delete(currentUser);
        return "User successfully Deleted with email: ".concat(currentUser.getEmail());
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

}
