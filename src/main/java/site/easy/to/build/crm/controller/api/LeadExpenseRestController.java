package site.easy.to.build.crm.controller.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import site.easy.to.build.crm.dto.LeadExpenseUpdateDto;
import site.easy.to.build.crm.entity.LeadExpense;
import site.easy.to.build.crm.entity.TriggerLeadHisto;
import site.easy.to.build.crm.service.lead.LeadExpenseService;

@RestController
@RequestMapping("/api/lead-expenses")
public class LeadExpenseRestController {

    private final LeadExpenseService leadExpenseService;

    @Autowired
    public LeadExpenseRestController(LeadExpenseService leadExpenseService) {
        this.leadExpenseService = leadExpenseService;
    }

    // Récupérer toutes les dépenses pour un lead
    // @GetMapping("/lead/{leadId}")
    // public ResponseEntity<List<LeadExpense>>
    // getExpensesByLeadId(@PathVariable("leadId") int leadId) {
    // List<LeadExpense> expenses = leadExpenseService.
    // return new ResponseEntity<>(expenses, HttpStatus.OK);
    // }

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

    // Créer une nouvelle dépense pour un lead
    // @PostMapping
    // public ResponseEntity<LeadExpense> createExpense(@RequestBody LeadExpense
    // leadExpense) {
    // LeadExpense createdExpense = leadExpenseService.createExpense(leadExpense);
    // return new ResponseEntity<>(createdExpense, HttpStatus.CREATED);
    // }

    // Mettre à jour une dépense existante
    // @PutMapping("/{id}")
    // public ResponseEntity<LeadExpense> updateExpense(@PathVariable("id") int id,
    // @RequestBody LeadExpense leadExpense) {
    // leadExpense.setId(id);
    // LeadExpense updatedExpense = leadExpenseService.updateExpense(leadExpense);
    // return updatedExpense != null ? new ResponseEntity<>(updatedExpense,
    // HttpStatus.OK)
    // : ResponseEntity.notFound().build();
    // }

    // // Supprimer une dépense
    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deleteExpense(@PathVariable("id") int id) {
    // boolean isDeleted = leadExpenseService.deleteExpense(id);
    // return isDeleted ? ResponseEntity.noContent().build() :
    // ResponseEntity.notFound().build();
    // }
}