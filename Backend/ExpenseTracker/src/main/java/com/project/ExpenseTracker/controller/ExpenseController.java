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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    @PostMapping("/add")
    public ResponseEntity<?> addExpense(@Valid @RequestBody RequestExpenseDTO requestExpenseDTO, BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .toList();

                return ResponseEntity.badRequest().body(errors);
            }
            ResponseExpenseDTO response = expenseService.addExpenseOfUser(requestExpenseDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (UserNotFound e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add-all")
    public ResponseEntity<?> addAllExpenses(@Valid @RequestBody List<RequestExpenseDTO> requestExpenseDTOList, BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                Set<String> errors = bindingResult.getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .collect(Collectors.toSet());

                return ResponseEntity.badRequest().body(errors);
            }
            List<ResponseExpenseDTO> response = expenseService.addAllExpenses(requestExpenseDTOList);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (UserNotFound e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllExpenses() {
        try {
            List<ResponseExpenseDTO> response = userService.getAllExpensesOfUser();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFound e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/filter")
    public ResponseEntity<?> getFilterExpenses(@RequestBody ExpenseFilterRequest expenseFilterRequest) {
        try {
            List<String> errors = expenseService.validateFilter(new Expense(), expenseFilterRequest);
            if (!errors.isEmpty()) return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            List<ResponseExpenseDTO> response = expenseService.getFilterExpenses(expenseFilterRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFound | SecurityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/update/{eid}")
    public ResponseEntity<?> updateExpenseOfUser(@PathVariable Long eid, @RequestBody Map<String, Object> updates) {
        try {
            List<String> errors = expenseService.validation(new ExpenseUpdateDTO(), updates);
            if (!errors.isEmpty()) return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            ResponseExpenseDTO response = expenseService.updateExpenseOfUser(eid, updates);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SecurityException | ExpenseNotFound e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{eid}")
    public ResponseEntity<?> deleteUserExpense(@PathVariable Long eid) {
        try {
            String response = userService.deleteExpenseOfUser(eid);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ExpenseNotFound | SecurityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getSummaryOfExpenses(@RequestParam(required = false) ExpenseCategory expenseCategory,
                                                  @RequestParam(required = false) String period) {
        try {
            List<ExpenseSummaryResponse> response = expenseService.getExpenseSummary(expenseCategory, period);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/expenses-to-csv")
    public void sendToCSV(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setContentType("text/csv");
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"expenses.csv\"");
        expenseService.exportExpenseToCSV(httpServletResponse.getWriter());
    }
}

