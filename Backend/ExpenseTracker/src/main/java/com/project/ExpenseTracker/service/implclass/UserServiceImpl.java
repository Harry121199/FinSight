package com.project.ExpenseTracker.service.implclass;

import com.project.ExpenseTracker.exception.ExpenseNotFound;
import com.project.ExpenseTracker.exception.UserAlreadyExists;
import com.project.ExpenseTracker.exception.UserNotFound;
import com.project.ExpenseTracker.model.Expense;
import com.project.ExpenseTracker.model.Users;
import com.project.ExpenseTracker.payload.expense.RequestExpenseDTO;
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

    @Autowired
    private AuthenticationManager authenticationManager;

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
    public List<ResponseExpenseDTO> getAllExpensesOfUser(Long uid) {
        Users user = userRepo.findById(uid)
                .orElseThrow(() -> new UserNotFound("user not found with the given id: " + uid));
        return user.getExpenses().stream()
                .map(expense -> modelMapper.map(expense, ResponseExpenseDTO.class))
                .toList();
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
    public ResponseUserDTO updateUserDetails(Long uid, Map<String, Object> updates) {
        Users users = userRepo.findById(uid)
                .orElseThrow(() -> new UserNotFound("user not found with ID: " + uid));
        mapToObject(updates, users);
        Users saved = userRepo.save(users);
        return modelMapper.map(saved, ResponseUserDTO.class);
    }

    @Override
    @Transactional
    public void deleteUser(Long uid, UserDeleteRequest userDeleteRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUsername = userDetails.getUsername();
        Users userToDelete = userRepo.findById(uid)
                .orElseThrow(() -> new UserNotFound("User not found with ID: " + uid));

        //email is username....
        if (!userToDelete.getEmail().equals(currentUsername)) {
            throw new SecurityException("Your are not authorized to delete this account");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            currentUsername,
                            userDeleteRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new SecurityException("You are not authorized to delete this account");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        userRepo.delete(userToDelete);
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

    public ResponseUserDTO convertToDto(Users user) {
        ResponseUserDTO userDTO = modelMapper.map(user, ResponseUserDTO.class);
        List<ResponseExpenseDTO> requestExpenseDTOS = user.getExpenses()
                .stream()
                .map(expense -> modelMapper.map(expense, ResponseExpenseDTO.class))
                .collect(Collectors.toList());
        userDTO.setExpenses(requestExpenseDTOS);
        return userDTO;
    }
}
