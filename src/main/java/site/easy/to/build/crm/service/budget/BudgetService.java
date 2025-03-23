package site.easy.to.build.crm.service.budget;

import java.util.List;

import org.springframework.stereotype.Service;

import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.repository.BudgetRepository;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public Budget save(Budget budget) {
        budgetRepository.save(budget);
        return budget;
    }

    public List<Budget> getCustomerBudgets(int customerId) {
        return budgetRepository.findByCustomerCustomerId(customerId);
    }
}
