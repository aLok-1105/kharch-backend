package com.kharch.kharch.repo;

import com.kharch.kharch.model.Budget;
import com.kharch.kharch.model.Category;
import com.kharch.kharch.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BudgetRepo extends JpaRepository<Budget, Long> {
    List<Budget> findAllByUserUserId(Long userId);

    @Query("""
        select b from Budget b
        where b.user.userId = :userId
        and b.startDate <= :transactionDate
        and b.endDate >= :transactionDate
        and b.category = :category
""")
    List<Budget> findBudgetByUserIdAndCategory(Long userId, Category category, LocalDate transactionDate);
//
//    @Query("""
//    update Budget b
//    set b.spend = :spendAmount
//    where b.budget_Id = :budgetId
//""")
//    Budget updateSpend(Long budgetId, BigDecimal spendAmount);
}
