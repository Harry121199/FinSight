package com.project.ExpenseTracker.controller;

import com.project.ExpenseTracker.enums.ExpenseCategory;
import com.project.ExpenseTracker.exception.BudgetAlreadyExists;
import com.project.ExpenseTracker.exception.UserNotFound;
import com.project.ExpenseTracker.payload.budget.RequestBudgetDTO;
import com.project.ExpenseTracker.payload.budget.ResponseBudgetDTO;
import com.project.ExpenseTracker.payload.budget.BudgetSummaryResponse;
import com.project.ExpenseTracker.service.abstractclass.BudgetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {
    @Autowired
    private BudgetService budgetService;

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllBudgetsOfUser() {
        try {
            List<ResponseBudgetDTO> response = budgetService.getAllBudgetsOfUser();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFound e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBudget(@Valid @RequestBody RequestBudgetDTO requestBudgetDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .collect(Collectors.toList());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        try {
            ResponseBudgetDTO response = budgetService.createBudget(requestBudgetDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (UserNotFound | BudgetAlreadyExists e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-all")
    public ResponseEntity<?> createAllBudget(@Valid @RequestBody List<RequestBudgetDTO> requestBudgetDTOS, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                Set<String> error = bindingResult.getFieldErrors().stream()
                        .map(fieldError -> fieldError.getField()+":"+fieldError.getDefaultMessage())
                        .collect(Collectors.toSet());
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
            List<ResponseBudgetDTO> response = budgetService.createAllBudgets(requestBudgetDTOS);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (UserNotFound | BudgetAlreadyExists e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("somthing went wrong!!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{bid}")
    public ResponseEntity<?> deleteBudgetOfUser(@PathVariable Long bid) {
        try {
            String response = budgetService.deleteBudgetOfUser(bid);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SecurityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getBudgetSummaryOfUser(@RequestParam String period,
                                                    @RequestParam(required = false) ExpenseCategory expenseCategory) {
        try {
            List<BudgetSummaryResponse> responses = budgetService.getBudgetSummary(period,expenseCategory);
            return new ResponseEntity<>(responses, HttpStatus.OK);
        } catch (UserNotFound e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("something went wrong!!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
