package com.project.ExpenseTracker.controller;

import com.project.ExpenseTracker.enums.ExpenseCategory;
import com.project.ExpenseTracker.exception.ExpenseNotFound;
import com.project.ExpenseTracker.exception.UserNotFound;
import com.project.ExpenseTracker.filter.ExpenseFilterRequest;
import com.project.ExpenseTracker.model.Expense;
import com.project.ExpenseTracker.payload.expense.RequestExpenseDTO;
import com.project.ExpenseTracker.payload.expense.ExpenseSummaryResponse;
import com.project.ExpenseTracker.payload.expense.ExpenseUpdateDTO;
import com.project.ExpenseTracker.payload.expense.ResponseExpenseDTO;
import com.project.ExpenseTracker.service.abstractclass.ExpenseService;
import com.project.ExpenseTracker.service.abstractclass.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/add/{uid}")
    public ResponseEntity<?> addExpense(@Valid @RequestBody RequestExpenseDTO requestExpenseDTO, @PathVariable Long uid, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();

            return ResponseEntity.badRequest().body(errors);
        }

        try {
            ResponseExpenseDTO response = expenseService.addExpenseOfUser(uid, requestExpenseDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (UserNotFound e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addall/{uid}")
    public ResponseEntity<?> addAllExpenses(@Valid @RequestBody List<RequestExpenseDTO> requestExpenseDTOList, @PathVariable Long uid, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Set<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toSet());

            return ResponseEntity.badRequest().body(errors);
        }

        try {
            List<ResponseExpenseDTO> response = expenseService.addAllExpenses(requestExpenseDTOList, uid);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (UserNotFound e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getallexpenses/{uid}")
    public ResponseEntity<?> getAllExpenses(@PathVariable Long uid) {
        try {
            List<ResponseExpenseDTO> response = userService.getAllExpensesOfUser(uid);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFound e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/filter/expenses/{uid}")
    public ResponseEntity<?> getFilterExpenses(@PathVariable Long uid, @RequestBody ExpenseFilterRequest expenseFilterRequest) {
        try {
            List<String> errors = expenseService.validateFilter(new Expense(), expenseFilterRequest);
            if (!errors.isEmpty()) return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            List<ResponseExpenseDTO> response = expenseService.getFilterExpenses(uid, expenseFilterRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFound | SecurityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>("something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateExpenseOfUser(@RequestParam Long uid, @RequestParam Long eid, @RequestBody Map<String, Object> updates) {


        try {
            List<String> errors = expenseService.validation(new ExpenseUpdateDTO(), updates);
            if (!errors.isEmpty()) return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            ResponseExpenseDTO response = expenseService.updateExpenseOfUser(uid, eid, updates);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SecurityException | ExpenseNotFound e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Something went wrong!!!!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/delete/expenses")
    public ResponseEntity<?> deleteUserExpense(@RequestParam Long uid, @RequestParam Long eid) {
        try {
            String response = userService.deleteExpenseOfUser(uid, eid);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ExpenseNotFound | SecurityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("something went wrong!!!!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{uid}/summary")
    public ResponseEntity<?> getSummaryOfExpenses(@PathVariable Long uid,
                                                  @RequestParam(required = false) ExpenseCategory expenseCategory,
                                                  @RequestParam(required = false) String period) {
        try {
            List<ExpenseSummaryResponse> response = expenseService.getExpenseSummary(uid, expenseCategory, period);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

