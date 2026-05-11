package com.kharch.kharch.repo;

import com.kharch.kharch.model.Category;
import com.kharch.kharch.model.PaymentMethod;
import com.kharch.kharch.model.Transaction;
import com.kharch.kharch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUserUserId(Long userId);

    @Query("""
            select t from Transaction t
                    where t.transactionDateTime >= :fromDate
                       and t.transactionDateTime < :toDate
            """
    )
    List<Transaction> findTransactionByDateRange(Instant fromDate, Instant toDate);

    List<Transaction> findAllByCategory(Category category);

    @Query(
            """
            select t from Transaction t
                        where t.user.userId = :userId
                                    and t.transactionDateTime >= :fromDate
                                    and t.transactionDateTime <= :toDate
                                    and (:cat IS NULL OR t.category = :cat)
                                    and (:method IS NULL OR t.paymentMethod = :method)
                        order by t.transactionDateTime DESC
            """
    )
    List<Transaction> search(Long userId, Category cat, PaymentMethod method, LocalDateTime fromDate, LocalDateTime toDate);

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.user.userId = :userId
          AND t.transactionDateTime >= :startDate
          AND t.transactionDateTime <=  :endDate
          AND (:category IS NULL OR t.category = :category)
    """)
    BigDecimal getAllSpendsOfUserByCategory(Long userId, Category category, LocalDateTime startDate, LocalDateTime endDate);

    @Query(""" 
        select t from Transaction t
        where t.user.userId = :userId
        and (:query IS NULL OR (
                :query = '' OR
                CAST(t.amount AS string) like LOWER(CONCAT('%', :query, '%')) OR
                lower(t.category) like LOWER(CONCAT('%', :query, '%')) OR
                lower(t.paymentMethod) like LOWER(CONCAT('%', :query, '%')) OR
                lower(t.description) like LOWER(CONCAT('%', :query, '%'))
        ))
        order by t.transactionDateTime DESC
    """)
    List<Transaction> findTransactionsBySearchQuery(Long userId, String query);

}
