package com.kharch.kharch.controller;

import com.kharch.kharch.dto.UpdateTransactionRequest;
import com.kharch.kharch.model.Category;
import com.kharch.kharch.model.PaymentMethod;
import com.kharch.kharch.model.Transaction;
import com.kharch.kharch.service.TransactionService;
import com.kharch.kharch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<String> createTransaction(@RequestBody Transaction transaction){
        return transactionService.createTransaction(transaction);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to){
        return transactionService.search(category, paymentMethod, from, to);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable("id") Long transactionId, @RequestBody UpdateTransactionRequest transaction){
        return transactionService.updateTransaction(transactionId, transaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable("id") Long transactionId){
        return transactionService.deleteTransaction(transactionId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Transaction>> findTransactionsBySearchQuery(@RequestParam String query){
        return transactionService.findTransactionsBySearchQuery(query);
    }


}
