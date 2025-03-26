package site.easy.to.build.crm.service.ticket;


import site.easy.to.build.crm.entity.TicketExpense;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TicketExpenseService {
    BigDecimal getTotalExpensesBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

    TicketExpense save(TicketExpense ticketExpense);

    TicketExpense getLatestExpenseForTicketHisto(int ticketHistoId);
}
