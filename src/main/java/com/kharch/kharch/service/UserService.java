package com.kharch.kharch.service;

import com.kharch.kharch.model.User;
import com.kharch.kharch.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;

@Service
public class UserService implements UserDetailsService {

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AuthService authService;

    public ResponseEntity<String> register(User user) {
        if(userRepo.findByEmail(user.getEmail()) != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists!");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        userRepo.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user.getEmail());
    }

    public String getRole(String email){
        User user = userRepo.findByEmail(email);
        if( user != null){
            return user.getRole().name();
        }
        return "USER";
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByEmail(username);
    }

    public ResponseEntity<String> updateUser(User user) {
        User oldUser = authService.getUserFromSecurityContext();

        if(oldUser != null){
            if(user.getEmail() != null) oldUser.setEmail(user.getEmail());
            if(user.getFullName() != null) oldUser.setFullName(user.getFullName());
            if(user.getCurrency() != null) oldUser.setCurrency(user.getCurrency());
            userRepo.save(oldUser);
            return ResponseEntity.status(HttpStatus.OK).body("Success");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    public ResponseEntity<String> deleteUser(Long userId) {
        User user = authService.getUserFromSecurityContext();
        if(user != null && Objects.equals(user.getUserId(), userId)){
            userRepo.deleteById(userId);
            return ResponseEntity.status(HttpStatus.OK).body("Deleted");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }
}
