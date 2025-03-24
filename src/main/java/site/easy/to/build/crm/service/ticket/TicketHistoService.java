package site.easy.to.build.crm.service.ticket;

import java.time.LocalDateTime;
import java.util.List;

import site.easy.to.build.crm.entity.TicketHisto;

public interface TicketHistoService {
    TicketHisto save(TicketHisto ticketHisto);

    List<TicketHisto> findAll();

    TicketHisto findByTicketHistoId(int id);

    public List<TicketHisto> getBetweenDate(LocalDateTime date1, LocalDateTime date2);
}
