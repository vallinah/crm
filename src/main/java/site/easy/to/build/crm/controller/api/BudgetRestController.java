package site.easy.to.build.crm.controller.api;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.TicketHisto;
import site.easy.to.build.crm.service.budget.BudgetService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/budget")
public class BudgetRestController {
    private final BudgetService budgetService;

    public BudgetRestController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/condition")
    public ResponseEntity<List<Budget>> getBudgetsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date1,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date2) {
        try {
            if (date1 == null || date2 == null) {
                throw new IllegalArgumentException("Les deux dates sont requises");
            }

            if (date1.isAfter(date2)) {
                throw new IllegalArgumentException("La date de début doit être avant la date de fin");
            }

            List<Budget> budgets = budgetService.getTriggerLeadHistoBetweenDates(date1, date2);
            return ResponseEntity.ok(budgets);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
