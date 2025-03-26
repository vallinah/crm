package site.easy.to.build.crm.service.lead;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import site.easy.to.build.crm.entity.LeadExpense;

public interface LeadExpenseService {
    public LeadExpense save(LeadExpense leadExpense);

    public LeadExpense findLatestByTriggerLeadHistoId(Integer triggerLeadHistoId);

    public LeadExpense findById(int id);

    BigDecimal getTotalExpensesBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

}
