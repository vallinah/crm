package site.easy.to.build.crm.service.ticket;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import site.easy.to.build.crm.entity.TicketHisto;
import site.easy.to.build.crm.repository.TicketHistoRepository;

@Service
public class TicketHistoServiceImpl implements TicketHistoService {
    private final TicketHistoRepository ticketHistoRepository;

    public TicketHistoServiceImpl(TicketHistoRepository ticketHistoRepository) {
        this.ticketHistoRepository = ticketHistoRepository;
    }

    @Override
    public List<TicketHisto> getAll() {
        return ticketHistoRepository.findByDeleteAtIsNull();
    }

    @Override
    public List<TicketHisto> getBetweenDate(LocalDateTime date1, LocalDateTime date2) {
        return ticketHistoRepository.getBetweenDate(date1, date2);
    }

    @Override
    public TicketHisto save(TicketHisto ticketHisto) {
        return ticketHistoRepository.save(ticketHisto);
    }

    @Override
    public List<TicketHisto> findAll() {
        return ticketHistoRepository.findAll();
    }

    @Override
    public TicketHisto findByTicketHistoId(int id) {
        return ticketHistoRepository.findById(id);
    };

}
