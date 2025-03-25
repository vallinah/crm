package site.easy.to.build.crm.service.database;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.ImportCustomer;
import site.easy.to.build.crm.entity.ImportLeadTicket;
import site.easy.to.build.crm.repository.ImportCustomerRepository;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.user.UserService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class ImportCustomerService {
    private final ImportCustomerRepository importCustomerRepository;
    private final UserService userService;
    private final CustomerService customerService;

    public ImportCustomerService(ImportCustomerRepository importCustomerRepository, UserService userService,
            CustomerService customerService) {
        this.importCustomerRepository = importCustomerRepository;
        this.userService = userService;
        this.customerService = customerService;
    }

    @Transactional
    public List<ImportCustomer> checkCsv(MultipartFile file) throws Exception {
        List<ImportCustomer> importCustomers = new ArrayList<>();
        List<String> errorLines = new ArrayList<>();
        Set<String> existingEmails = new HashSet<>();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

        int lineNumber = 1;
        for (CSVRecord record : csvParser) {
            List<String> lineErrors = new ArrayList<>();
            ImportCustomer importCustomer = new ImportCustomer();

            // Validation de l'email
            try {
                String email = record.get("customer_email");
                if (email == null || email.trim().isEmpty()) {
                    lineErrors.add("Email client manquant");
                } else if (!isValidEmail(email)) {
                    lineErrors.add("Format email client invalide");
                } else if (existingEmails.contains(email)) {
                    lineErrors.add("Email en double");
                } else {
                    importCustomer.setCustomerEmail(email.trim());
                    existingEmails.add(email.trim());
                }
            } catch (Exception e) {
                lineErrors.add("Erreur de lecture de l'email client");
            }

            // Validation du nom
            try {
                String name = record.get("customer_name");
                if (name == null || name.trim().isEmpty()) {
                    lineErrors.add("Nom client manquant");
                } else if (name.length() > 100) {
                    lineErrors.add("Nom client trop long (max 100 caractères)");
                } else {
                    importCustomer.setCustomerName(name.trim());
                }
            } catch (Exception e) {
                lineErrors.add("Erreur de lecture du nom client");
            }

            if (lineErrors.isEmpty()) {
                importCustomers.add(importCustomer);
            } else {
                errorLines.add("Ligne " + lineNumber + " : " + String.join(", ", lineErrors));
            }
            lineNumber++;
        }

        if (!errorLines.isEmpty()) {
            throw new Exception("Échec de l'import : \n" + String.join("\n", errorLines));
        }
        return importCustomers;
    }

    private boolean isValidEmail(String email) {
        if (email == null)
            return false;

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email.trim()).matches();
    }

    public void importCSV(List<ImportCustomer> importCustomers) throws Exception {
        for (ImportCustomer importCustomer : importCustomers) {
            Customer customer = new Customer();
            customer.setEmail(importCustomer.getCustomerEmail());
            customer.setName(importCustomer.getCustomerName());
            customer.setUser(userService.findById(52));
            customer.setAddress("Andoharanofotsy");
            customer.setCountry("Madagascar");
            customerService.save(customer);
        }
    }
}
