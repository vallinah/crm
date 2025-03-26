package site.easy.to.build.crm.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.TicketHisto;
import site.easy.to.build.crm.entity.TriggerLeadHisto;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.service.ticket.TicketHistoService;
import site.easy.to.build.crm.service.ticket.TicketService;
import site.easy.to.build.crm.util.AuthorizationUtil;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketRestController {

    @Autowired
    private TicketHistoService ticketHistoService;

    @Autowired
    private TicketService ticketService;

    // Récupérer un ticket par son ID
    @GetMapping("/{id}")
    public ResponseEntity<TicketHisto> getTicketById(@PathVariable int id) {
        TicketHisto ticket = ticketHistoService.findByTicketHistoId(id);
        if (ticket != null) {
            return ResponseEntity.ok(ticket);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete-ticket/{id}")
    public ResponseEntity<String> deleteTicket(@PathVariable("id") int id) {
        try {
            Ticket ticket = ticketService.findByTicketId(id);
            TicketHisto ticketHisto = ticketHistoService.findByTicketHistoId(id);

            if (ticket == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket non trouvé avec l'ID : " + id);
            }

            ticketService.delete(ticket);
            ticketHisto.setDeleteAt(LocalDateTime.now());
            ticketHistoService.save(ticketHisto);

            return ResponseEntity.ok("Ticket supprimé avec succès.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression du ticket : " + ex.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<TicketHisto>> getAllHistorique() {
        List<TicketHisto> ticketHistos = ticketHistoService.getAll();
        return ResponseEntity.ok(ticketHistos);
    }

    @GetMapping("/condition")
    public ResponseEntity<List<TicketHisto>> getAllHistoriqueBetweenDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date1,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date2) {
        try {
            // Validation des dates
            if (date1 == null || date2 == null) {
                throw new IllegalArgumentException("Les deux dates sont requises");
            }

            if (date1.isAfter(date2)) {
                throw new IllegalArgumentException("La date de début doit être avant la date de fin");
            }

            List<TicketHisto> ticketHistos = ticketHistoService.getBetweenDate(date1, date2);
            return ResponseEntity.ok(ticketHistos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}