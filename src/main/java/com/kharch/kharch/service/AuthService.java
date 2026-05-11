package com.kharch.kharch.service;

import com.kharch.kharch.model.User;
import com.kharch.kharch.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AuthService {

    @Autowired
    private UserRepo userRepo;

    public User getUserFromSecurityContext(){
        return userRepo.findByEmail((String) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal());
    }
}
