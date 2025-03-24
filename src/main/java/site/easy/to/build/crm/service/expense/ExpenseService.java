package site.easy.to.build.crm.service.expense;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import site.easy.to.build.crm.entity.TicketExpense;
import site.easy.to.build.crm.entity.TicketHisto;
import site.easy.to.build.crm.entity.TriggerLeadHisto;
import site.easy.to.build.crm.repository.LeadExpenseRepository;
import site.easy.to.build.crm.repository.TicketExpenseRepository;
import site.easy.to.build.crm.repository.TicketHistoRepository;
import site.easy.to.build.crm.repository.TriggerLeadHistoRepository;

@Service
public class ExpenseService {
    private final TicketExpenseRepository ticketExpenseRepository;
    private final TicketHistoRepository ticketHistoRepository;
    private final TriggerLeadHistoRepository triggerLeadHistoRepository;
    private final LeadExpenseRepository leadExpenseRepository;

    public ExpenseService(TicketExpenseRepository ticketExpenseRepository, TicketHistoRepository ticketHistoRepository,
            TriggerLeadHistoRepository triggerLeadHistoRepository, LeadExpenseRepository leadExpenseRepository) {
        this.ticketExpenseRepository = ticketExpenseRepository;
        this.ticketHistoRepository = ticketHistoRepository;
        this.triggerLeadHistoRepository = triggerLeadHistoRepository;
        this.leadExpenseRepository = leadExpenseRepository;
    }

    public BigDecimal getCustomerDepense(int idCustomer) {
        BigDecimal totalDepense = BigDecimal.ZERO;

        List<TicketHisto> ticketHistos = ticketHistoRepository.findByCustomerCustomerId(idCustomer);
        List<TriggerLeadHisto> triggerLeadHistos = triggerLeadHistoRepository.findByCustomerCustomerId(idCustomer);

        for (TicketHisto ticketHisto : ticketHistos) {
            Optional<TicketExpense> latestExpense = ticketExpenseRepository.findByIdHistoDateMax(ticketHisto.getId());
            if (latestExpense.isPresent()) {
                totalDepense = totalDepense.add(latestExpense.get().getAmount());
            }
        }
        for (TriggerLeadHisto triggerLeadHisto : triggerLeadHistos) {
            totalDepense = totalDepense
                    .add(leadExpenseRepository.findLatestByTriggerLeadHistoId(triggerLeadHisto.getId()).getAmount());
        }
        return totalDepense;
    }
}
