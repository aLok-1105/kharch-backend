package com.kharch.kharch.controller;

import com.kharch.kharch.dto.LoginRequestDto;
import com.kharch.kharch.dto.UserResponseDto;
import com.kharch.kharch.model.User;
import com.kharch.kharch.service.JwtService;
import com.kharch.kharch.service.UserService;
import com.kharch.kharch.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody User user){
        return userService.register(user);
    }

    @PostMapping("login")
    public ResponseEntity<UserResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
        Authentication authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));

        if(authentication.isAuthenticated()){
            User user = userRepo.findByEmail(loginRequestDto.getEmail());
            UserResponseDto userDetails = new UserResponseDto(
                user.getFullName(),
                user.getEmail(),
                jwtService.generateToken(user.getEmail(), 
                userService.getRole(user.getEmail())), 
                user.getUserId());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(userDetails);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("health")
    public ResponseEntity<String> healthCheck(){
        return ResponseEntity.status(HttpStatus.OK).body("Application is running");
    }

    @PutMapping("userUpdate")
    public ResponseEntity<String> updateUser(@RequestBody User user){
        return userService.updateUser(user);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long userId){
        return userService.deleteUser(userId);
    }


}
