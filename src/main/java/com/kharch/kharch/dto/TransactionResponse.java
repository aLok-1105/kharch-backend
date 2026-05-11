//package com.kharch.kharch.dto;
//
//import com.kharch.kharch.model.Category;
//import com.kharch.kharch.model.PaymentMethod;
//import com.kharch.kharch.model.Transaction;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//public record TransactionResponse(
//        Long id,
//        BigDecimal amount,
//        Category category,
//        String description,
//        LocalDateTime transactionDateTime,
//        PaymentMethod paymentMethod,
//        String notes,
//        LocalDateTime createdAt,
//        LocalDateTime updatedAt
//) {
//    public static TransactionResponse from(Transaction t) {
//        return new TransactionResponse(
//                t.getTransaction_Id(),
//                t.getAmount(),
//                t.getCategory(),
//                t.getDescription(),
//                t.getTransactionDateTime(),
//                t.getPaymentMethod(),
//                t.getCreatedAt(),
//                t.getUpdatedAt()
//        );
//    }
//}
