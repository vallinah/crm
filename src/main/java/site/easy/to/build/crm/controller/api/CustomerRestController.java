package site.easy.to.build.crm.controller.api;

import org.springframework.web.bind.annotation.*;

import site.easy.to.build.crm.controller.CustomerController;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.service.customer.CustomerService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerRestController {

    private CustomerService customerService;

    // Récupérer un client par son ID
    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable int customerId) {
        Customer customer = customerService.findByCustomerId(customerId);
        if (customer != null) {
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Récupérer tous les clients d'un utilisateur spécifique
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Customer>> getCustomersByUserId(@PathVariable int userId) {
        List<Customer> customers = customerService.findByUserId(userId);
        return ResponseEntity.ok(customers);
    }

    // Récupérer un client par son email
    @GetMapping("/email/{email}")
    public ResponseEntity<Customer> getCustomerByEmail(@PathVariable String email) {
        Customer customer = customerService.findByEmail(email);
        if (customer != null) {
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Récupérer tous les clients
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.findAll();
        return ResponseEntity.ok(customers);
    }

    // Créer un nouveau client
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer savedCustomer = customerService.save(customer);
        return ResponseEntity.ok(savedCustomer);
    }

    // Supprimer un client
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable int customerId) {
        Customer customer = customerService.findByCustomerId(customerId);
        if (customer != null) {
            customerService.delete(customer);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Récupérer les clients récents pour un utilisateur
    @GetMapping("/recent/{userId}")
    public ResponseEntity<List<Customer>> getRecentCustomers(
            @PathVariable int userId,
            @RequestParam(defaultValue = "5") int limit) {
        List<Customer> recentCustomers = customerService.getRecentCustomers(userId, limit);
        return ResponseEntity.ok(recentCustomers);
    }

    // Compter le nombre de clients pour un utilisateur
    @GetMapping("/count/{userId}")
    public ResponseEntity<Long> countCustomersByUserId(@PathVariable int userId) {
        long count = customerService.countByUserId(userId);
        return ResponseEntity.ok(count);
    }
}