package site.easy.to.build.crm.service.ticket;

import site.easy.to.build.crm.entity.TicketHisto;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketHistoService {
    List<TicketHisto> getAll();

    List<TicketHisto> getBetweenDate (LocalDateTime date1, LocalDateTime date2);

    TicketHisto save(TicketHisto ticketHisto);

    List<TicketHisto> findAll();

    TicketHisto findByTicketHistoId(int id);
}
