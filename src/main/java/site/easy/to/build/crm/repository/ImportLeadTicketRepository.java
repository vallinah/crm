package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.easy.to.build.crm.entity.ImportLeadTicket;

public interface ImportLeadTicketRepository extends JpaRepository<ImportLeadTicket, Long> {
}
