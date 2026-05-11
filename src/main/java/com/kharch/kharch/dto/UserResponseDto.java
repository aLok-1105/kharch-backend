package com.kharch.kharch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserResponseDto {
    private String fullName;
    private String email;
    private String token;
    private Long userId;
}
