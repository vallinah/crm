package site.easy.to.build.crm.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import site.easy.to.build.crm.entity.LeadExpense;
import site.easy.to.build.crm.entity.TriggerLeadHisto;
import site.easy.to.build.crm.service.lead.LeadExpenseService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import site.easy.to.build.crm.dto.LeadExpenseUpdateDto;

@RestController
@RequestMapping("/api/lead-expenses")
public class LeadExpenseRestController {

    private final LeadExpenseService leadExpenseService;

    @Autowired
    public LeadExpenseRestController(LeadExpenseService leadExpenseService) {
        this.leadExpenseService = leadExpenseService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeadExpense> getExpenseById(@PathVariable("id") int id) {
        Optional<LeadExpense> expense = Optional.ofNullable(leadExpenseService.findLatestByTriggerLeadHistoId(id));
        return expense.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/leadExpense/{id}")
    public ResponseEntity<LeadExpense> getExpenseByIdLead(@PathVariable("id") int id) {
        Optional<LeadExpense> expense = Optional.ofNullable(leadExpenseService.findById(id));
        return expense.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalExpenses(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        BigDecimal total = leadExpenseService.getTotalExpensesBetweenDates(startDate, endDate);
        return ResponseEntity.ok(total);
    }

    @PutMapping("/update")
    public ResponseEntity<LeadExpense> updateLeadExpense(@RequestBody LeadExpenseUpdateDto updateDto) {
        try {
            // 1. Valider les données reçues
            if (updateDto == null) {
                return ResponseEntity.badRequest().build();
            }
            TriggerLeadHisto leadhisto = new TriggerLeadHisto();
            leadhisto.setId(updateDto.getLeadId());
            // 3. Mettre à jour les champs
            LeadExpense expenseToUpdate = new LeadExpense();
            expenseToUpdate.setAmount(updateDto.getAmount());
            expenseToUpdate.setCreatedAt(updateDto.getCreatedAt());
            expenseToUpdate.setTriggerLeadHisto(leadhisto);
            LeadExpense updatedExpense = leadExpenseService.save(expenseToUpdate);

            // 5. Retourner la réponse
            return ResponseEntity.ok(updatedExpense);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}