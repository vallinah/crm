package site.easy.to.build.crm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.entity.TicketHisto;

@Repository
public interface TicketHistoRepository extends JpaRepository<TicketHisto, Integer> {
    public TicketHisto findById(int ticketId);

    public List<TicketHisto> findByCustomerCustomerId(int customerId);
}
