package com.kharch.kharch.controller;

import com.kharch.kharch.dto.BudgetRequest;
import com.kharch.kharch.model.Budget;
import com.kharch.kharch.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @GetMapping
    public ResponseEntity<List<Budget>> getAllBudgets(){
        return budgetService.getAllBudgets();
    }

    @PostMapping
    public ResponseEntity<String> createBudget(@RequestBody Budget budget){
        return budgetService.createBudget(budget);
    }

    @PutMapping("{id}")
    public ResponseEntity<String> updateBudget(@PathVariable("id") Long budgetId , @RequestBody BudgetRequest budgetRequest){
        return budgetService.updateBudget(budgetId, budgetRequest);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteBudget(@PathVariable("id") Long budgetId){
        return budgetService.deleteBudget(budgetId);
    }

}
