package com.kharch.kharch.service;

import com.kharch.kharch.dto.BudgetRequest;
import com.kharch.kharch.model.Budget;
import com.kharch.kharch.model.Category;
import com.kharch.kharch.model.Period;
import com.kharch.kharch.model.User;
import com.kharch.kharch.repo.BudgetRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepo budgetRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private TransactionService transactionService;

    private User getUserIdFromSecurityContext(){
        return authService.getUserFromSecurityContext();
    }

    public LocalDate getEndDate(LocalDate startDate, Period period, Long days){
        if(period == null || period == Period.MONTHLY){
            return startDate.plusMonths(1);
        }
        else if(period == Period.WEEKLY) {
            return startDate.plusWeeks(1);
        }
        else if(period == Period.YEARLY){
            return startDate.plusYears(1);
        }
        else if(period == Period.DAILY){
            return startDate.plusDays(1);
        }
        else {
            if(days == null){
                return null;
            }
            return startDate.plusDays(days);
        }
    }

    public ResponseEntity<List<Budget>> getAllBudgets() {
        User user = getUserIdFromSecurityContext();
        if(user != null){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(budgetRepo.findAllByUserUserId(user.getUserId()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
    }

    public ResponseEntity<String> createBudget(Budget budget) {
        User user = getUserIdFromSecurityContext();
        if(user != null){
            budget.setUser(user);
            budget.setEndDate(getEndDate(budget.getStartDate(), budget.getPeriod(), budget.getDays()));
            budget.setActive(true);
            budget.setSpend(transactionService.getAllSpendsOfUserByCategory(user, budget.getCategory(), budget.getStartDate(), budget.getEndDate()));
            budgetRepo.save(budget);
            System.out.println("Budget Saved : " + budget);
            return ResponseEntity.status(HttpStatus.OK).body("Saved");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user Found!");
    }


    public ResponseEntity<String> updateBudget(Long budgetId, BudgetRequest budgetRequest) {
        Budget budget = budgetRepo.findById(budgetId).orElse(null);
        if(budget != null){
            User user = getUserIdFromSecurityContext();
            System.out.println(user);
            System.out.println(budget);
            if(user != null && Objects.equals(user.getUserId(), budget.getUser().getUserId())){
                if(budgetRequest.active() != null) budget.setActive(budgetRequest.active());
                if(budgetRequest.amount() != null) budget.setAmount(budgetRequest.amount());
                if(budgetRequest.description() != null) budget.setDescription(budgetRequest.description());
                if(budgetRequest.period() != null) {
                    budget.setPeriod(budgetRequest.period());
                    budget.setEndDate(getEndDate(budget.getStartDate(), budgetRequest.period(), budget.getDays()));
                }
                if(budgetRequest.startDate() != null) {
                    budget.setStartDate(budgetRequest.startDate());
                    budget.setEndDate(getEndDate(budgetRequest.startDate(), budget.getPeriod(), budget.getDays()));
                }
                if(budgetRequest.days() != null){
                    if(budget.getPeriod() == Period.OTHER){
                        budget.setEndDate(getEndDate(budget.getStartDate(), budget.getPeriod(), budgetRequest.days()));
                        budget.setDays(budgetRequest.days());
                    }
                    else{
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The period must be other to update the days!");
                    }
                }

                budget.setSpend(transactionService.getAllSpendsOfUserByCategory(user, budget.getCategory(), budget.getStartDate(), budget.getEndDate()));

                budgetRepo.save(budget);

                return ResponseEntity.status(HttpStatus.OK).body("Updated!");
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not Authorized!");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Budget Not Found!");
    }

    public ResponseEntity<String> deleteBudget(Long budgetId) {
        Budget budget = budgetRepo.findById(budgetId).orElse(null);
        if(budget != null){
            User user = getUserIdFromSecurityContext();
            if(user != null && Objects.equals(user.getUserId(), budget.getUser().getUserId())){
                budgetRepo.deleteById(budgetId);
                return ResponseEntity.status(HttpStatus.OK).body("Deleted!");
            }
            else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not Authorized!");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Budget Not Found!");
    }
}
