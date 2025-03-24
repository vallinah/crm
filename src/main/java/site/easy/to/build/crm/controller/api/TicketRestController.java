package site.easy.to.build.crm.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.TicketHisto;
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
    public ResponseEntity<List<TicketHisto>> getAllHistorique(
            @RequestParam(required = false) LocalDateTime date1,
            @RequestParam(required = false) LocalDateTime date2) {
        List<TicketHisto> ticketHistos = ticketHistoService.getBetweenDate(date1, date2);
        return ResponseEntity.ok(ticketHistos);
    }
}