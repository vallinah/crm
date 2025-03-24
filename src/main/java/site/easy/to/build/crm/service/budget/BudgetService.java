package site.easy.to.build.crm.service.budget;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.RateConfig;
import site.easy.to.build.crm.repository.BudgetRepository;
import site.easy.to.build.crm.service.rate.RateConfigService;
import site.easy.to.build.crm.service.expense.ExpenseService;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final ExpenseService expenseService;
    private final RateConfigService rateConfigService;

    public BudgetService(BudgetRepository budgetRepository, ExpenseService expenseService,
            RateConfigService rateConfigService) {
        this.budgetRepository = budgetRepository;
        this.expenseService = expenseService;
        this.rateConfigService = rateConfigService;
    }

    public Budget save(Budget budget) {
        budgetRepository.save(budget);
        return budget;
    }

    public List<Budget> getCustomerBudgets(int customerId) {
        return budgetRepository.findByCustomerCustomerId(customerId);
    }

    public BigDecimal getTotalCustomerBudgets(int customerId) {
        List<Budget> budgets = getCustomerBudgets(customerId);
        BigDecimal totalBudget = BigDecimal.ZERO;
        for (Budget budget : budgets) {
            totalBudget = totalBudget.add(budget.getAmount());
        }
        return totalBudget;
    }

    public BigDecimal getRealBudget(int customerId) {
        return getTotalCustomerBudgets(customerId).subtract(expenseService.getCustomerDepense(customerId));
    }

    public boolean isBudgetTargetReached(int customerId, BigDecimal add) {
        return (expenseService.getCustomerDepense(customerId).add(add))
                .compareTo(getTotalCustomerBudgets(customerId)) > 0;
    }

    public boolean isRateAlertReached(int customerId, BigDecimal add) {
        Optional<RateConfig> rateConfig = rateConfigService.findLatest();
        BigDecimal tauxAlert = rateConfig.get().getRate();

        BigDecimal totalBudgets = getTotalCustomerBudgets(customerId);
        BigDecimal totalExpenses = expenseService.getCustomerDepense(customerId).add(add);

        if (totalBudgets.compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }

        BigDecimal pourcentageDepense = totalExpenses
                .multiply(new BigDecimal("100"))
                .divide(totalBudgets, 2, RoundingMode.HALF_UP);
        System.out.println("budget: " + totalBudgets + "");
        System.out.println("expense: " + totalExpenses + "");
        System.out.println("tauxAlert: " + tauxAlert + "");

        return pourcentageDepense.compareTo(tauxAlert) >= 0;
    }
}
