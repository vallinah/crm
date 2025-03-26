package site.easy.to.build.crm.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import site.easy.to.build.crm.entity.TicketExpense;

@Repository
public interface TicketExpenseRepository extends JpaRepository<TicketExpense, Integer> {
        @Query(value = "SELECT * FROM ticket_expense WHERE ticket_histo_id = :idTicketHisto ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
        Optional<TicketExpense> findByIdHistoDateMax(@Param("idTicketHisto") int idTicketHisto);

        @Query("SELECT COALESCE(SUM(te.amount), 0.00) FROM TicketExpense te " +
                        "WHERE (:startDate IS NULL OR te.createdAt >= :startDate) " +
                        "AND (:endDate IS NULL OR te.createdAt <= :endDate)")
        BigDecimal sumAmountBetweenDates(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);
}
