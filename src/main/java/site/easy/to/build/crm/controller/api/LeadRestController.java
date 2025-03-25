package site.easy.to.build.crm.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.TriggerLeadHisto;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.lead.TriggerLeadHistoService;

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
    // // Créer un nouveau lead
    // @PostMapping
    // public ResponseEntity<Lead> createLead(@RequestBody Lead lead) {
    // Lead createdLead = leadService.createLead(lead);
    // return new ResponseEntity<>(createdLead, HttpStatus.CREATED);
    // }

    // Mettre à jour un lead
    // @PutMapping("/{id}")
    // public ResponseEntity<Lead> updateLead(@PathVariable("id") int id,
    // @RequestBody Lead lead) {
    // lead.setLeadId(id);
    // Lead updatedLead = leadService.updateLead(lead);
    // return updatedLead != null ? new ResponseEntity<>(updatedLead, HttpStatus.OK)
    // : ResponseEntity.notFound().build();
    // }
}
