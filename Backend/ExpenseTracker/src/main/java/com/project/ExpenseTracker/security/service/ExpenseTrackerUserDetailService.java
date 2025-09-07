package com.project.ExpenseTracker.security.service;

import com.project.ExpenseTracker.exception.UserNotFound;
import com.project.ExpenseTracker.model.Users;
import com.project.ExpenseTracker.repository.UserRepo;
import com.project.ExpenseTracker.security.model.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ExpenseTrackerUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = userRepo.findByEmail(username)
                .orElseThrow(() -> new UserNotFound("User not found with username: ".concat(username)));
        return new AuthUser(users);
    }
}
