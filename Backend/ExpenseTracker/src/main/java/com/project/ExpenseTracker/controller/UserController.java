package com.project.ExpenseTracker.controller;

import com.project.ExpenseTracker.exception.UserNotFound;
import com.project.ExpenseTracker.payload.user.ResponseUserDTO;
import com.project.ExpenseTracker.payload.user.UserDeleteRequest;
import com.project.ExpenseTracker.payload.user.UserUpdateDTO;
import com.project.ExpenseTracker.service.abstractclass.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PatchMapping("/update/{uid}")
    public ResponseEntity<?> updateUserDetails(@PathVariable Long uid, @RequestBody Map<String, Object> updates) {
        try {
            List<String> errors = userService.validation(updates, new UserUpdateDTO());
            if (!errors.isEmpty()) return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            ResponseUserDTO response = userService.updateUserDetails(uid, updates);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFound e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{uid}")
    public ResponseEntity<?> deleteUser(@PathVariable Long uid
            , @Valid @RequestBody UserDeleteRequest userDeleteRequest
            , BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getFieldErrors().stream()
                        .map(fieldError -> {
                            return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                        }).toList();
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
            userService.deleteUser(uid, userDeleteRequest);
            return new ResponseEntity<>("User Deleted Successfully ID: " + uid, HttpStatus.OK);
        }catch (SecurityException | UserNotFound | BadCredentialsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
