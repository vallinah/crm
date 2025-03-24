package site.easy.to.build.crm.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.service.lead.LeadService;

@RestController
@RequestMapping("/api/leads")
public class LeadRestController {

    @Autowired
    private LeadService leadService;

    // Récupérer un lead par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Lead> getLeadById(@PathVariable int id) {
        Lead lead = leadService.findByLeadId(id);
        if (lead != null) {
            return ResponseEntity.ok(lead);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Récupérer tous les leads
    @GetMapping
    public ResponseEntity<List<Lead>> getAllLeads() {
        List<Lead> leads = leadService.findAll();
        return ResponseEntity.ok(leads);
    }

    // Récupérer les leads assignés à un utilisateur
    @GetMapping("/assigned/{userId}")
    public ResponseEntity<List<Lead>> getAssignedLeads(@PathVariable int userId) {
        List<Lead> leads = leadService.findAssignedLeads(userId);
        return ResponseEntity.ok(leads);
    }

    // Récupérer les leads créés par un utilisateur
    @GetMapping("/created/{userId}")
    public ResponseEntity<List<Lead>> getCreatedLeads(@PathVariable int userId) {
        List<Lead> leads = leadService.findCreatedLeads(userId);
        return ResponseEntity.ok(leads);
    }

    // Récupérer un lead par son ID de réunion
    @GetMapping("/meeting/{meetingId}")
    public ResponseEntity<Lead> getLeadByMeetingId(@PathVariable String meetingId) {
        Lead lead = leadService.findByMeetingId(meetingId);
        if (lead != null) {
            return ResponseEntity.ok(lead);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Créer un nouveau lead
    @PostMapping
    public ResponseEntity<Lead> createLead(@RequestBody Lead lead) {
        Lead savedLead = leadService.save(lead);
        return ResponseEntity.ok(savedLead);
    }

    // Supprimer un lead
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable int id) {
        Lead lead = leadService.findByLeadId(id);
        if (lead != null) {
            leadService.delete(lead);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Récupérer les leads récents pour un manager
    @GetMapping("/recent/manager/{managerId}")
    public ResponseEntity<List<Lead>> getRecentLeadsForManager(
            @PathVariable int managerId,
            @RequestParam(defaultValue = "5") int limit) {
        List<Lead> recentLeads = leadService.getRecentLeads(managerId, limit);
        return ResponseEntity.ok(recentLeads);
    }

    // Récupérer les leads d'un client spécifique
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Lead>> getLeadsByCustomerId(@PathVariable int customerId) {
        List<Lead> leads = leadService.getCustomerLeads(customerId);
        return ResponseEntity.ok(leads);
    }

    // Compter le nombre de leads assignés à un employé
    @GetMapping("/count/employee/{employeeId}")
    public ResponseEntity<Long> countLeadsByEmployeeId(@PathVariable int employeeId) {
        long count = leadService.countByEmployeeId(employeeId);
        return ResponseEntity.ok(count);
    }

    // Compter le nombre de leads assignés à un manager
    @GetMapping("/count/manager/{managerId}")
    public ResponseEntity<Long> countLeadsByManagerId(@PathVariable int managerId) {
        long count = leadService.countByManagerId(managerId);
        return ResponseEntity.ok(count);
    }

    // Compter le nombre de leads pour un client
    @GetMapping("/count/customer/{customerId}")
    public ResponseEntity<Long> countLeadsByCustomerId(@PathVariable int customerId) {
        long count = leadService.countByCustomerId(customerId);
        return ResponseEntity.ok(count);
    }

    // Récupérer les leads récents pour un employé
    @GetMapping("/recent/employee/{employeeId}")
    public ResponseEntity<List<Lead>> getRecentLeadsByEmployee(
            @PathVariable int employeeId,
            @RequestParam(defaultValue = "5") int limit) {
        List<Lead> recentLeads = leadService.getRecentLeadsByEmployee(employeeId, limit);
        return ResponseEntity.ok(recentLeads);
    }

    // Récupérer les leads récents pour un client
    @GetMapping("/recent/customer/{customerId}")
    public ResponseEntity<List<Lead>> getRecentLeadsByCustomer(
            @PathVariable int customerId,
            @RequestParam(defaultValue = "5") int limit) {
        List<Lead> recentLeads = leadService.getRecentCustomerLeads(customerId, limit);
        return ResponseEntity.ok(recentLeads);
    }

    // Supprimer tous les leads d'un client
    @DeleteMapping("/customer/{customerId}")
    public ResponseEntity<Void> deleteLeadsByCustomer(@PathVariable int customerId) {
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        leadService.deleteAllByCustomer(customer);
        return ResponseEntity.noContent().build();
    }
}
