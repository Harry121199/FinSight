package com.project.ExpenseTracker.controller;

import com.project.ExpenseTracker.exception.UserNotFound;
import com.project.ExpenseTracker.exception.UserNotInGroup;
import com.project.ExpenseTracker.payload.expense.RequestExpenseDTO;
import com.project.ExpenseTracker.payload.expense.ResponseExpenseDTO;
import com.project.ExpenseTracker.payload.expenseGroup.RequestGroupDTO;
import com.project.ExpenseTracker.payload.expenseGroup.ResponseGroupDTO;
import com.project.ExpenseTracker.payload.user.ResponseUserDTO;
import com.project.ExpenseTracker.payload.user.UserNameDTO;
import com.project.ExpenseTracker.service.abstractclass.ExpenseGroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class ExpenseGroupController {

    @Autowired
    private ExpenseGroupService expenseGroupService;

    @PostMapping("/create")
    public ResponseEntity<?> createGroup(@Valid @RequestBody RequestGroupDTO requestGroupDTO, BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getFieldErrors().stream()
                        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .toList();
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
            ResponseGroupDTO response = expenseGroupService.createGroup(requestGroupDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (SecurityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add-all/expense/{gid}")
    public ResponseEntity<?> addExpensesInGroup(@PathVariable Long gid, @Valid @RequestBody List<RequestExpenseDTO> requestExpenseDTOList, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getFieldErrors().stream()
                        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .toList();
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
            List<ResponseExpenseDTO> response = expenseGroupService.addAllExpenses(gid, requestExpenseDTOList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotInGroup | UserNotFound | SecurityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add/expense/{gid}")
    public ResponseEntity<?> addExpenseInGroup(@PathVariable Long gid, @Valid @RequestBody RequestExpenseDTO requestExpenseDTO, BindingResult bindingResult) {
        try {
            if(bindingResult.hasErrors()){
                List<String> errors = bindingResult.getFieldErrors().stream()
                        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .toList();
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
            ResponseExpenseDTO response = expenseGroupService.addExpense(gid, requestExpenseDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SecurityException | UserNotFound | UserNotInGroup e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add/user/{gid}")
    public ResponseEntity<?> addUserInGroup(
            @PathVariable Long gid,
            @Valid @RequestBody UserNameDTO userNameDTO,
            BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getFieldErrors().stream()
                        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .toList();
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
            String response = expenseGroupService.addUser(userNameDTO, gid);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SecurityException | UserNotFound | UserNotInGroup e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/users/{gid}")
    public ResponseEntity<?> getAllUser(@PathVariable Long gid) {
        try {
            List<ResponseUserDTO> response = expenseGroupService.getAllUser(gid);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SecurityException | UserNotFound | UserNotInGroup e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/expense/{gid}")
    public ResponseEntity<?> getAllExpense(@PathVariable Long gid) {
        try {
            List<ResponseExpenseDTO> response = expenseGroupService.getAllExpense(gid);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SecurityException | UserNotFound | UserNotInGroup e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/remove/user/{gid}")
    public ResponseEntity<?> removeUser(@PathVariable Long gid, @Valid @RequestBody UserNameDTO userNameDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getFieldErrors().stream()
                        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .toList();
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
            String response = expenseGroupService.removeUser(userNameDTO, gid);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SecurityException | UserNotFound | UserNotInGroup e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/remove/expense")
    public ResponseEntity<?> removeExpense(@RequestParam Long eid, @RequestParam Long gid) {
        try {
            String response = expenseGroupService.removeExpense(eid,gid);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SecurityException | UserNotFound | UserNotInGroup e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
