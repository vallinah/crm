package site.easy.to.build.crm.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.TicketHisto;
import site.easy.to.build.crm.entity.TriggerLeadHisto;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.lead.TriggerLeadHistoService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/leads")
public class LeadRestController {

    private final TriggerLeadHistoService leadService;
    private final LeadService leadService2;

    @Autowired
    public LeadRestController(TriggerLeadHistoService leadService, LeadService leadService2) {
        this.leadService = leadService;
        this.leadService2 = leadService2;
    }

    // Récupérer tous les leads
    @GetMapping("/all")
    public ResponseEntity<List<TriggerLeadHisto>> getAllLeads() {
        List<TriggerLeadHisto> leads = leadService.getAll();
        return new ResponseEntity<>(leads, HttpStatus.OK);
    }

    // Récupérer un lead par son ID
    @GetMapping("/{id}")
    public ResponseEntity<TriggerLeadHisto> getLeadById(@PathVariable("id") int id) {
        TriggerLeadHisto lead = leadService.getById(id);
        if (lead != null) {
            return ResponseEntity.ok(lead);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Supprimer un lead
    @DeleteMapping("/delete-lead/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable("id") int id) {
        Lead lead = leadService2.findByLeadId(id);
        leadService2.delete(lead);
        leadService.delete(id);
        leadService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/condition")
    public ResponseEntity<List<TriggerLeadHisto>> getAllHistoriqueBetweenDate(
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

            List<TriggerLeadHisto> ticketHistos = leadService.getTriggerLeadHistoBetweenDates(date1, date2);
            return ResponseEntity.ok(ticketHistos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}