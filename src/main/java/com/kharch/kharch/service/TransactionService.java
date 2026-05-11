package com.kharch.kharch.service;

import com.kharch.kharch.dto.UpdateTransactionRequest;
import com.kharch.kharch.model.*;
import com.kharch.kharch.repo.BudgetRepo;
import com.kharch.kharch.repo.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private BudgetRepo budgetRepo;

    private void updateSpend(Long userId, Category category, BigDecimal spend, LocalDate transactionDate){
        List<Budget> budgetList = budgetRepo.findBudgetByUserIdAndCategory(userId, category, transactionDate);

        if(!budgetList.isEmpty()) {
            for(Budget budget : budgetList){
                BigDecimal newSpend = budget.getSpend().add(spend);
                budget.setSpend(newSpend);
                budgetRepo.save(budget);
            }
        }
    }

    public ResponseEntity<List<Transaction>> getAllTransactions() {
        User user = authService.getUserFromSecurityContext();

        if(user != null){
            List<Transaction> transactions = transactionRepo.findAllByUserUserId(user.getUserId());
            return ResponseEntity.status(HttpStatus.OK).body(transactions);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    public ResponseEntity<String> createTransaction(Transaction transaction) {
        User user = authService.getUserFromSecurityContext();
        if(user != null){
            transaction.setUser(user);
            transactionRepo.save(transaction);
            updateSpend(user.getUserId(), transaction.getCategory(), transaction.getAmount(), transaction.getTransactionDateTime().toLocalDate());
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction.getAmount().toString());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }

    public ResponseEntity<Transaction> updateTransaction(Long transactionId, UpdateTransactionRequest transaction) {
        User user = authService.getUserFromSecurityContext();
        Transaction oldTransaction = transactionRepo.findById(transactionId).orElse(null);

        if(oldTransaction == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        if(!Objects.equals(oldTransaction.getUser().getUserId(), user.getUserId())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        if (transaction.category() != null)            oldTransaction.setCategory(transaction.category());
        if (transaction.description() != null)         oldTransaction.setDescription(transaction.description());
        if (transaction.transactionDateTime() != null) oldTransaction.setTransactionDateTime(transaction.transactionDateTime());
        if (transaction.paymentMethod() != null)       oldTransaction.setPaymentMethod(transaction.paymentMethod());
        if (transaction.amount() != null)              {
            updateSpend(user.getUserId(), transaction.category(), transaction.amount().subtract(oldTransaction.getAmount()), transaction.transactionDateTime().toLocalDate());
            oldTransaction.setAmount(transaction.amount());
        }

        return ResponseEntity.status(HttpStatus.OK).body(transactionRepo.save(oldTransaction));

    }

    public ResponseEntity<String> deleteTransaction(Long transactionId) {
        User user = authService.getUserFromSecurityContext();
        Transaction transaction = transactionRepo.findById(transactionId).orElse(null);
        if(transaction == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Transaction not found");
        }
        if(!Objects.equals(transaction.getUser().getUserId(), user.getUserId())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not Authorized");
        }
        transactionRepo.deleteById(transactionId);
        return  ResponseEntity.status(HttpStatus.OK).body("Deleted");
    }

    public ResponseEntity<List<Transaction>> getTransactionsByDate(String from, String to) {
        Instant fromDate = LocalDate.parse(from).atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant toDate = LocalDate.parse(to).atStartOfDay(ZoneId.systemDefault()).toInstant();

        return ResponseEntity.status(HttpStatus.OK).body(transactionRepo.findTransactionByDateRange(fromDate, toDate));

    }

    public ResponseEntity<List<Transaction>> getTransactionsByCategory(String category) {
        try{
            Category cat = Category.valueOf(category.toUpperCase().trim());
            return ResponseEntity.status(HttpStatus.OK).body(transactionRepo.findAllByCategory(cat));
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }

    }

    public ResponseEntity<List<Transaction>> search(Category category, PaymentMethod paymentMethod, String from, String to) {

        String effectiveFromDate = "1900-01-01T00:00:00";

        LocalDateTime fromDate = !Objects.equals(from, null) ? LocalDate.parse(from).atStartOfDay()
                :  LocalDateTime.parse(effectiveFromDate);
        LocalDateTime toDate = !Objects.equals(to, null)  ? LocalDate.parse(to).atStartOfDay()
                : LocalDateTime.now();

        if(fromDate.isAfter(toDate)){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }

        User user = authService.getUserFromSecurityContext();

        return ResponseEntity.status(HttpStatus.OK).body(transactionRepo.search(user.getUserId(), category, paymentMethod, fromDate, toDate));

    }

    public BigDecimal getAllSpendsOfUserByCategory(User user, Category category, LocalDate startDate, LocalDate endDate){

        return transactionRepo.getAllSpendsOfUserByCategory(user.getUserId(), category, startDate.atStartOfDay(), endDate.atStartOfDay());
    }

    public ResponseEntity<List<Transaction>> findTransactionsBySearchQuery(String query){
        User user = authService.getUserFromSecurityContext();
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(transactionRepo.findTransactionsBySearchQuery(user.getUserId(), query));
    }
}
