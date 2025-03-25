package site.easy.to.build.crm.service.database;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.ImportBudgetCustomer;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.repository.BudgetRepository;
import site.easy.to.build.crm.repository.ImportBudgetCustomerRepository;
import site.easy.to.build.crm.repository.UserRepository;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.user.UserService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportBudgetCustomerService {
    private final ImportBudgetCustomerRepository importBudgetCustomerRepository;
    private final BudgetRepository budgetRepository;
    private final UserService userService;
    private final CustomerService customerService;

    public ImportBudgetCustomerService(ImportBudgetCustomerRepository importBudgetCustomerRepository,
            BudgetRepository budgetRepository, UserService userService,
            CustomerService customerService) {
        this.importBudgetCustomerRepository = importBudgetCustomerRepository;
        this.budgetRepository = budgetRepository;
        this.userService = userService;
        this.customerService = customerService;
    }

    @Transactional
    public List<ImportBudgetCustomer> checkCsv(MultipartFile file) throws Exception {
        List<ImportBudgetCustomer> importBudgetCustomers = new ArrayList<>();
        List<String> errorLines = new ArrayList<>();

        // Lire le fichier CSV
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

        // verification des donee
        int lineNumber = 1;
        for (CSVRecord record : csvParser) {
            try {
                ImportBudgetCustomer importBudgetCustomer = new ImportBudgetCustomer();
                importBudgetCustomer.setCustomerEmail(record.get("customer_email"));
                importBudgetCustomer.setAmount(parseAmount(record.get("Budget"))); // Utilisation de BigDecimal
                importBudgetCustomers.add(importBudgetCustomer);
            } catch (Exception e) {
                errorLines.add("Ligne " + lineNumber + " : " + e.getMessage());
            }
            lineNumber++;
        }

        // Si des erreurs sont survenues, les lister et les envoyer
        if (!errorLines.isEmpty()) {
            throw new Exception("Import failed at : " + errorLines);
        }
        return importBudgetCustomers;
    }

    public void importCsv(List<ImportBudgetCustomer> importBudgetCustomers) throws Exception {
        for (ImportBudgetCustomer importBudgetCustomer : importBudgetCustomers) {
            Budget budget = new Budget();
            budget.setAmount(importBudgetCustomer.getAmount());
            budget.setCreatedAt(LocalDate.now());
            budget.setCustomer(customerService.findByEmail(importBudgetCustomer.getCustomerEmail()));
            budgetRepository.save(budget);
        }
    }

    public BigDecimal parseAmount(String amountStr) {
        // Remplacer la virgule par un point pour traiter le format européen
        String formattedAmount = amountStr.replace(',', '.');

        // Convertir la chaîne en BigDecimal
        try {
            return new BigDecimal(formattedAmount);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format: " + amountStr, e);
        }
    }
}
