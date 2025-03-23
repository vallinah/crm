package site.easy.to.build.crm.service.ticket;

import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.TicketHisto;
import site.easy.to.build.crm.repository.TicketHistoRepository;

import java.util.List;

@Service
public class TicketHistoServiceImpl implements TicketHistoService{
    private final TicketHistoRepository ticketHistoRepository;

    public TicketHistoServiceImpl(TicketHistoRepository ticketHistoRepository) {
        this.ticketHistoRepository = ticketHistoRepository;
    }

    @Override
    public TicketHisto save(TicketHisto ticketHisto){
        return  ticketHistoRepository.save(ticketHisto);
    }

    @Override
    public List<TicketHisto> findAll(){
        return ticketHistoRepository.findAll();
    }

    @Override
    public TicketHisto findByTicketHistoId(int id){return ticketHistoRepository.findById(id);};

}
