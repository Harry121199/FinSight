package com.project.ExpenseTracker.controller;

import com.project.ExpenseTracker.exception.UserNotFound;
import com.project.ExpenseTracker.payload.dashboard.DashBoardDTO;
import com.project.ExpenseTracker.payload.dashboard.MonthlyTrendDTO;
import com.project.ExpenseTracker.service.abstractclass.ExpenseService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/summary")
    public ResponseEntity<?> getDashBoardSummary(
            @RequestParam(required = false)
            @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "Period must be in YYYY-MM format")
            String period) {
        try {
            DashBoardDTO response = expenseService.gerUserSummary(period);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SecurityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/months-summary")
    public ResponseEntity<?> getMonthlyTrendForYear(
            @Max(value = 12, message = "Last 12 months can be send only.")
            @RequestParam(defaultValue = "6")
            Integer months) {
        try {
            List<MonthlyTrendDTO> response = expenseService.getMonthlyTrendForYear(months);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SecurityException | UserNotFound e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
