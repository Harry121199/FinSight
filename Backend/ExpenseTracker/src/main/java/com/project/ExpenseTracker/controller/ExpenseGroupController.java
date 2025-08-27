package com.project.ExpenseTracker.controller;

import com.project.ExpenseTracker.payload.expenseGroup.RequestGroupDTO;
import com.project.ExpenseTracker.payload.expenseGroup.ResponseGroupDTO;
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

    @PostMapping("/{uid}/create")
    public ResponseEntity<?> createGroup(@PathVariable Long uid, @Valid @RequestBody RequestGroupDTO requestGroupDTO, BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getFieldErrors().stream()
                        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .toList();
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
            ResponseGroupDTO response = expenseGroupService.createGroup(uid, requestGroupDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (SecurityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
