package com.kharch.kharch.dto;

import com.kharch.kharch.model.Category;
import com.kharch.kharch.model.PaymentMethod;
import com.kharch.kharch.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateTransactionRequest(
        BigDecimal amount,
        Category category,
        String description,
        LocalDateTime transactionDateTime,
        PaymentMethod paymentMethod
) {

}
