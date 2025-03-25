package site.easy.to.build.crm.service.lead;

import java.util.Optional;

import org.springframework.stereotype.Service;

import site.easy.to.build.crm.entity.LeadExpense;
import site.easy.to.build.crm.repository.LeadExpenseRepository;

@Service
public class LeadExpenseServiceImpl implements LeadExpenseService {
    private final LeadExpenseRepository leadExpenseRepository;

    public LeadExpenseServiceImpl(LeadExpenseRepository leadExpenseRepository) {
        this.leadExpenseRepository = leadExpenseRepository;
    }

    @Override
    public LeadExpense save(LeadExpense leadExpense) {
        return leadExpenseRepository.save(leadExpense);
    }

    @Override
    public LeadExpense findLatestByTriggerLeadHistoId(Integer triggerLeadHistoId) {
        return leadExpenseRepository.findLatestByTriggerLeadHistoId(triggerLeadHistoId);
    }

    @Override
    public LeadExpense findById(int id) {
        Optional<LeadExpense> result = leadExpenseRepository.findById(id);

        if (result.isEmpty()) {
            throw new RuntimeException("Dépense non trouvée avec l'ID: " + id);
        }

        return result.get();
    }
}
