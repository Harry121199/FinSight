package com.project.ExpenseTracker.controller;


import com.project.ExpenseTracker.payload.recurringExpenses.RequestRecurringExpenseDTO;
import com.project.ExpenseTracker.payload.recurringExpenses.ResponseRecurringExpenseDTO;
import com.project.ExpenseTracker.service.abstractclass.RecurringExpenseService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recurringExpense")
public class RecurringExpenseController {

    @Autowired
    private RecurringExpenseService recurringExpenseService;

    @PostMapping("/create")
    public ResponseEntity<?> createRecurringExpense(@Valid @RequestBody RequestRecurringExpenseDTO requestRecurringExpenseDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getFieldErrors().stream()
                        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .toList();
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
            ResponseRecurringExpenseDTO response = recurringExpenseService.createRecurringExpense(requestRecurringExpenseDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SecurityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/create-all")
    public ResponseEntity<?> createAllRecurringExpense(@Valid @RequestBody List<RequestRecurringExpenseDTO> recurringExpenseDTOS, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getFieldErrors().stream()
                        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .toList();
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
            List<ResponseRecurringExpenseDTO> response = recurringExpenseService.createAllRecurringExpenses(recurringExpenseDTOS);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SecurityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllRecurringExpenses() {
        try {
            List<ResponseRecurringExpenseDTO> response = recurringExpenseService.getAllRecurringService();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{rid}")
    public ResponseEntity<?> deleteRecurringExpenseById(@PathVariable Long rid) {
        try {
            String response = recurringExpenseService.deleteRecurringExpenseById(rid);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SecurityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
