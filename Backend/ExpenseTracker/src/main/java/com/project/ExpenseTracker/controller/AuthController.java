package com.project.ExpenseTracker.controller;

import com.project.ExpenseTracker.exception.UserAlreadyExists;
import com.project.ExpenseTracker.payload.UserDTO;
import com.project.ExpenseTracker.security.jwt.JwtToken;
import com.project.ExpenseTracker.security.payload.JwtAuthRequest;
import com.project.ExpenseTracker.security.payload.JwtAuthResponse;
import com.project.ExpenseTracker.service.abstractclass.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtToken jwtToken;

    @PostMapping("/register")
    public ResponseEntity<?> signup(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();

            return ResponseEntity.badRequest().body(errors);
        }
        UserDTO response;
        try {
            response = userService.createUser(userDTO);
        } catch (UserAlreadyExists e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtAuthRequest jwtAuthRequest) {
        try{
            UserDetails userDetails = authenticate(jwtAuthRequest.getEmail(), jwtAuthRequest.getPassword());
            JwtAuthResponse jwtAuthResponse = new JwtAuthResponse(jwtToken.generateJwtToken(userDetails));
            return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private UserDetails authenticate(String email, String password) {
        UsernamePasswordAuthenticationToken userpass = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManager.authenticate(userpass);
        return (UserDetails) authentication.getPrincipal();
    }

}
