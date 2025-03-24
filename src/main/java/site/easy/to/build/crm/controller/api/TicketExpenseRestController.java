package site.easy.to.build.crm.controller.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import site.easy.to.build.crm.entity.TicketExpense;
import site.easy.to.build.crm.service.ticket.TicketExpenseService;

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

    // Mettre à jour une dépense
    @PutMapping("/modif/{id}")
    public ResponseEntity<TicketExpense> update(@PathVariable int id,
            @Valid @RequestBody TicketExpense updatedTicketExpense) {
        TicketExpense existingTicketExpense = ticketExpenseService.getLatestExpenseForTicketHisto(id);

        if (existingTicketExpense == null) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

        existingTicketExpense.setAmount(updatedTicketExpense.getAmount());
        existingTicketExpense.setCreatedAt(updatedTicketExpense.getCreatedAt());
        existingTicketExpense.setTicketHisto(updatedTicketExpense.getTicketHisto());

        TicketExpense savedTicketExpense = ticketExpenseService.save(existingTicketExpense);
        return ResponseEntity.ok(savedTicketExpense); // 200 OK
    }

}