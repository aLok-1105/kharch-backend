package com.kharch.kharch.dto;

import com.kharch.kharch.model.Period;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BudgetRequest(
        BigDecimal amount,
        String description,
        Period period,
        Long days,
        Boolean active,
        LocalDate startDate
) {
}
