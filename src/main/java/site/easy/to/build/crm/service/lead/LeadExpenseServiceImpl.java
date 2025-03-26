package site.easy.to.build.crm.service.lead;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Override
    public BigDecimal getTotalExpensesBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        // Si les deux dates sont null, on retourne la somme totale
        if (startDate == null && endDate == null) {
            return leadExpenseRepository.sumAmountBetweenDates(null, null);
        }

        // Si seule la date de fin est null, on prend tout depuis startDate
        if (endDate == null) {
            return leadExpenseRepository.sumAmountBetweenDates(startDate, null);
        }

        // Si seule la date de début est null, on prend tout jusqu'à endDate
        if (startDate == null) {
            return leadExpenseRepository.sumAmountBetweenDates(null, endDate);
        }

        // Les deux dates sont renseignées
        return leadExpenseRepository.sumAmountBetweenDates(startDate, endDate);
    }
}
