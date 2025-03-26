package site.easy.to.build.crm.controller.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.entity.TicketExpense;
import site.easy.to.build.crm.service.ticket.TicketExpenseService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/ticket-expenses")
public class TicketExpenseRestController {

    @Autowired
    private TicketExpenseService ticketExpenseService;

    // Récupérer une dépense par son ID
    @GetMapping("/{id}")
    public ResponseEntity<TicketExpense> getById(@PathVariable int id) {
        // Récupération de la dépense à partir du service
        TicketExpense ticketExpense = ticketExpenseService.getLatestExpenseForTicketHisto(id);

        if (ticketExpense != null) {
            // Si une dépense est trouvée, la retourner avec un statut HTTP 200
            return ResponseEntity.ok(ticketExpense);
        } else {
            // Si aucune dépense n'est trouvée, retourner une dépense avec des valeurs par
            // défaut
            TicketExpense defaultTicketExpense = new TicketExpense();
            defaultTicketExpense.setId(0);
            defaultTicketExpense.setAmount(BigDecimal.ZERO); // Utilisation de BigDecimal.ZERO pour un montant par
                                                             // défaut
            defaultTicketExpense.setCreatedAt(LocalDateTime.MIN); // Valeur par défaut pour la date de création

            return ResponseEntity.ok(defaultTicketExpense);
        }
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalExpenses(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        BigDecimal total = ticketExpenseService.getTotalExpensesBetweenDates(startDate, endDate);
        return ResponseEntity.ok(total);
    }

    // Mettre à jour une dépense
    @PutMapping("/modif/{id}")
    public ResponseEntity<TicketExpense> update(@PathVariable int id,
            @Valid @RequestBody TicketExpense updatedTicketExpense) {
        TicketExpense existingTicketExpense = new TicketExpense();

        existingTicketExpense.setAmount(updatedTicketExpense.getAmount());
        existingTicketExpense.setCreatedAt(updatedTicketExpense.getCreatedAt());
        existingTicketExpense.setTicketHisto(updatedTicketExpense.getTicketHisto());

        TicketExpense savedTicketExpense = ticketExpenseService.save(existingTicketExpense);
        return ResponseEntity.ok(savedTicketExpense); // 200 OK
    }

}