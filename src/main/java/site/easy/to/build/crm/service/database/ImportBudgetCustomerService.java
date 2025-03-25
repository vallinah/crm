package site.easy.to.build.crm.service.database;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.ImportBudgetCustomer;
import site.easy.to.build.crm.entity.ImportCustomer;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

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

    // @Transactional
    // public List<ImportBudgetCustomer> checkCsv(MultipartFile file) throws
    // Exception {
    // List<ImportBudgetCustomer> importBudgetCustomers = new ArrayList<>();
    // List<String> errorLines = new ArrayList<>();
    // Set<String> existingEmails = new HashSet<>();

    // BufferedReader reader = new BufferedReader(
    // new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
    // CSVParser csvParser = new CSVParser(reader,
    // CSVFormat.DEFAULT.withFirstRecordAsHeader());

    // int lineNumber = 1;
    // for (CSVRecord record : csvParser) {
    // List<String> lineErrors = new ArrayList<>();
    // ImportBudgetCustomer importBudgetCustomer = new ImportBudgetCustomer();

    // // Vérification de l'email
    // try {
    // String email = record.get("customer_email");
    // if (email == null || email.trim().isEmpty()) {
    // lineErrors.add("Email client manquant");
    // } else if (!isValidEmail(email)) {
    // lineErrors.add("Format email client invalide");
    // } else {
    // email = email.trim();
    // importBudgetCustomer.setCustomerEmail(email);

    // // Vérification que le customer existe
    // if (customerService.findByEmail(email) == null) {
    // lineErrors.add("Aucun client trouvé avec cet email");
    // }
    // }
    // } catch (Exception e) {
    // lineErrors.add("Erreur de lecture de l'email client");
    // }

    // // Vérification du budget
    // try {
    // String amountStr = record.get("Budget");
    // if (amountStr == null || amountStr.trim().isEmpty()) {
    // lineErrors.add("Montant budget manquant");
    // } else {
    // BigDecimal amount = parseAmount(amountStr);
    // if (amount.compareTo(BigDecimal.ZERO) <= 0) {
    // lineErrors.add("Le montant doit être positif");
    // } else {
    // importBudgetCustomer.setAmount(amount);
    // }
    // }
    // } catch (Exception e) {
    // lineErrors.add("Format montant budget invalide: " + e.getMessage());
    // }

    // // Si aucune erreur sur cette ligne, ajouter à la liste
    // if (lineErrors.isEmpty()) {
    // importBudgetCustomers.add(importBudgetCustomer);
    // } else {
    // errorLines.add("Ligne " + lineNumber + " : " + String.join(", ",
    // lineErrors));
    // }

    // lineNumber++;
    // }

    // if (!errorLines.isEmpty()) {
    // throw new Exception("Échec de l'import : \n" + String.join("\n",
    // errorLines));
    // }
    // return importBudgetCustomers;
    // }

    @Transactional
    public List<ImportBudgetCustomer> checkCsv(MultipartFile file, List<ImportCustomer> importCustomers)
            throws Exception {
        List<ImportBudgetCustomer> importBudgetCustomers = new ArrayList<>();
        List<String> errorLines = new ArrayList<>();

        // Créer un set des emails valides (base de données + fichier customer)
        Set<String> validEmails = new HashSet<>();

        // Ajouter les emails existants en base
        List<Customer> existingCustomers = customerService.findAll();
        existingCustomers.forEach(c -> validEmails.add(c.getEmail().trim().toLowerCase()));

        // Ajouter les emails du fichier customer à importer
        if (importCustomers != null) {
            importCustomers.forEach(c -> validEmails.add(c.getCustomerEmail().trim().toLowerCase()));
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

        int lineNumber = 1;
        for (CSVRecord record : csvParser) {
            List<String> lineErrors = new ArrayList<>();
            ImportBudgetCustomer importBudgetCustomer = new ImportBudgetCustomer();

            // Vérification de l'email
            try {
                String email = record.get("customer_email");
                if (email == null || email.trim().isEmpty()) {
                    lineErrors.add("Email client manquant");
                } else if (!isValidEmail(email)) {
                    lineErrors.add("Format email client invalide");
                } else {
                    email = email.trim().toLowerCase();
                    importBudgetCustomer.setCustomerEmail(email);

                    // Vérification que l'email existe dans la base OU dans le fichier customer
                    if (!validEmails.contains(email)) {
                        lineErrors.add("Aucun client trouvé avec cet email (ni en base ni dans l'import)");
                    }
                }
            } catch (Exception e) {
                lineErrors.add("Erreur de lecture de l'email client");
            }

            // Vérification du budget (inchangée)
            try {
                String amountStr = record.get("Budget");
                if (amountStr == null || amountStr.trim().isEmpty()) {
                    lineErrors.add("Montant budget manquant");
                } else {
                    BigDecimal amount = parseAmount(amountStr);
                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        lineErrors.add("Le montant doit être positif");
                    } else {
                        importBudgetCustomer.setAmount(amount);
                    }
                }
            } catch (Exception e) {
                lineErrors.add("Format montant budget invalide: " + e.getMessage());
            }

            if (lineErrors.isEmpty()) {
                importBudgetCustomers.add(importBudgetCustomer);
            } else {
                errorLines.add("Ligne " + lineNumber + " : " + String.join(", ", lineErrors));
            }
            lineNumber++;
        }

        if (!errorLines.isEmpty()) {
            throw new Exception("Échec de l'import Budget : \n" + String.join("\n", errorLines));
        }
        return importBudgetCustomers;
    }

    // public void importCsv(List<ImportBudgetCustomer> importBudgetCustomers)
    // throws Exception {
    // for (ImportBudgetCustomer importBudgetCustomer : importBudgetCustomers) {
    // // Pas besoin de re-vérifier ici car déjà fait dans checkCsv
    // Budget budget = new Budget();
    // budget.setAmount(importBudgetCustomer.getAmount());
    // budget.setCreatedAt(LocalDate.now());
    // budget.setCustomer(customerService.findByEmail(importBudgetCustomer.getCustomerEmail()));
    // budgetRepository.save(budget);
    // }
    // }

    // Méthode pour valider le format d'email
    private boolean isValidEmail(String email) {
        if (email == null)
            return false;

        // Expression régulière simple pour validation d'email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email.trim()).matches();
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
