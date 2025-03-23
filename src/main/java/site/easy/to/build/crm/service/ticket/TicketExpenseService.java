package site.easy.to.build.crm.service.ticket;


import site.easy.to.build.crm.entity.TicketExpense;

public interface TicketExpenseService {
    TicketExpense save(TicketExpense ticketExpense);

    TicketExpense getLatestExpenseForTicketHisto(int ticketHistoId);
}
