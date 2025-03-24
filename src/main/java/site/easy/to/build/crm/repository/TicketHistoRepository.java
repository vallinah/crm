package site.easy.to.build.crm.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import site.easy.to.build.crm.entity.TicketHisto;

@Repository
public interface TicketHistoRepository extends JpaRepository<TicketHisto, Integer> {
    public TicketHisto findById(int ticketId);

    public List<TicketHisto> findByCustomerCustomerId(int customerId);

    @Query("SELECT t FROM TicketHisto t " +
            "WHERE t.deleteAt IS NULL " +
            "AND (:date1 IS NULL OR t.createdAt >= :date1) " +
            "AND (:date2 IS NULL OR t.createdAt <= :date2)")
    List<TicketHisto> getBetweenDate(
            @Param("date1") LocalDateTime date1,
            @Param("date2") LocalDateTime date2);
}
