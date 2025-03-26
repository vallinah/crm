package site.easy.to.build.crm.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.easy.to.build.crm.entity.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    public List<Budget> findByCustomerCustomerId(int customerId);

    @Query("SELECT b FROM Budget b " +
            "WHERE (:startDate IS NULL AND :endDate IS NULL) " +  // Si les deux dates sont null
            "OR (b.createdAt BETWEEN COALESCE(:startDate, b.createdAt) AND COALESCE(:endDate, b.createdAt))")
    List<Budget> findBudgetsBetweenDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
