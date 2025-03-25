package site.easy.to.build.crm.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import site.easy.to.build.crm.entity.ImportBudgetCustomer;
import site.easy.to.build.crm.entity.ImportCustomer;
import site.easy.to.build.crm.entity.ImportLeadTicket;
import site.easy.to.build.crm.service.customer.CustomerImportService;
import site.easy.to.build.crm.service.database.DatabaseService;
import site.easy.to.build.crm.service.database.ImportBudgetCustomerService;
import site.easy.to.build.crm.service.database.ImportCustomerService;
import site.easy.to.build.crm.service.database.ImportLeadTicketService;

@Controller
@RequestMapping("/database")
public class DatabaseController {
    @Autowired
    private DatabaseService databaseService;
    private final CustomerImportService importService;
    private final ImportLeadTicketService importLeadTicketService;
    private final ImportCustomerService importCustomerService;
    private final ImportBudgetCustomerService importBudgetCustomerService;

    public DatabaseController(DatabaseService databaseService, CustomerImportService importService,
            ImportLeadTicketService importLeadTicketService, ImportCustomerService importCustomerService,
            ImportBudgetCustomerService importBudgetCustomerService) {
        this.databaseService = databaseService;
        this.importService = importService;
        this.importLeadTicketService = importLeadTicketService;
        this.importCustomerService = importCustomerService;
        this.importBudgetCustomerService = importBudgetCustomerService;
    }

    @GetMapping("/reset-database")
    public String resetPage() {
        return "database/reset-database";
    }

    @GetMapping("/import-csv")
    public String importPage() {
        return "database/import-csv";
    }

    @PostMapping("/reset-database")
    public String resetDatabase(RedirectAttributes redirectAttributes) {
        databaseService.resetDatabase();
        redirectAttributes.addFlashAttribute("message", "La base de données a été réinitialisée avec succès !");
        return "redirect:/";
    }

    // @PostMapping("/customers/import")
    // public String handleFileUpload(@RequestParam("file") MultipartFile file,
    // Model model) {

    // if (file.isEmpty()) {
    // model.addAttribute("error", "Le fichier est vide");
    // return "database/import-csv";
    // }

    // if (!Objects.equals(file.getContentType(), "text/csv")) {
    // model.addAttribute("error", "Seuls les fichiers CSV sont acceptés");
    // return "database/import-csv";
    // }

    // try {
    // CustomerImportService.ImportResult result =
    // importService.importCustomersFromCsv(file);
    // model.addAttribute("success",
    // String.format("Importation réussie: %d clients importés",
    // result.getSavedCount()));
    // } catch (CustomerImportService.ImportException e) {
    // model.addAttribute("error", e.getMessage());
    // model.addAttribute("errors", e.getErrors());
    // model.addAttribute("fileName", file.getOriginalFilename());
    // }

    // return "database/import-csv";
    // }

    @PostMapping("/import-csv")
    public String importLeadTicketCsvFile(
            @RequestParam("LeadTicketFile") MultipartFile leadTicketFile,
            @RequestParam("CustomerFile") MultipartFile customerFile,
            @RequestParam("BudgetCustomerFile") MultipartFile budgetCustomerFile,
            RedirectAttributes redirectAttributes, Model model) throws Exception {

        List<String> errorMessages = new ArrayList<>();
        List<ImportBudgetCustomer> importBudgetCustomers = new ArrayList<>();
        List<ImportCustomer> importCustomers = new ArrayList<>();
        List<ImportLeadTicket> importLeadTickets = new ArrayList<>();
        try {
            importCustomers = importCustomerService.checkCsv(customerFile);
        } catch (Exception e) {
            errorMessages
                    .add("Erreur dans le fichier '" + customerFile.getOriginalFilename() + "' : " + e.getMessage());
            errorMessages.add("\n");
        }

        try {
            importLeadTickets = importLeadTicketService.checkCsv(leadTicketFile, importCustomers);
        } catch (Exception e) {
            errorMessages
                    .add("Erreur dans le fichier '" + leadTicketFile.getOriginalFilename() + "' : " + e.getMessage());
            errorMessages.add("\n");
        }

        try {
            importCustomers = importCustomerService.checkCsv(customerFile);
            importBudgetCustomers = importBudgetCustomerService.checkCsv(budgetCustomerFile, importCustomers);
        } catch (Exception e) {
            errorMessages.add(
                    "Erreur dans le fichier '" + budgetCustomerFile.getOriginalFilename() + "' : " + e.getMessage());
            errorMessages.add("\n");
        }

        if (!errorMessages.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", String.join(" | ", errorMessages));
            System.out.println("Erreurs rencontrées : " + errorMessages);
            return "redirect:/database/import-csv"; // Retourne avec les erreurs
        }

        importCustomerService.importCSV(importCustomers);
        importBudgetCustomerService.importCsv(importBudgetCustomers);
        importLeadTicketService.importCsv(importLeadTickets);

        redirectAttributes.addFlashAttribute("message", "Import réussi !");
        System.out.println("Succès de l'import");
        return "redirect:/database/import-csv"; // Redirection en cas de succès

    }
}
