package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.entity.TicketExpense;

import java.util.Optional;

@Repository
public interface TicketExpenseRepository extends JpaRepository<TicketExpense, Integer> {
    @Query(value = "SELECT * FROM ticket_expense WHERE ticket_histo_id = :idTicketHisto ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<TicketExpense> findByIdHistoDateMax(@Param("idTicketHisto") int idTicketHisto);
}
