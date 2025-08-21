package com.project.ExpenseTracker.controller;

import com.project.ExpenseTracker.exception.UserNotFound;
import com.project.ExpenseTracker.payload.UserDTO;
import com.project.ExpenseTracker.payload.UserUpdateDTO;
import com.project.ExpenseTracker.service.abstractclass.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PatchMapping("/update/{uid}")
    public ResponseEntity<?> updateUserDetails(@PathVariable Long uid,@RequestBody Map<String, Object> updates) {
        UserDTO response;
        try{
            List<String> errors = userService.validation(updates, new UserUpdateDTO());
            if(!errors.isEmpty()) return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            response = userService.updateUserDetails(uid, updates);
        }catch (UserNotFound e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
