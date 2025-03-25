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
import java.util.List;

@Service
public class ImportCustomerService {
    private final ImportCustomerRepository importCustomerRepository;
    private final UserService userService;
    private final CustomerService customerService;

    public ImportCustomerService(ImportCustomerRepository importCustomerRepository, UserService userService , CustomerService customerService) {
        this.importCustomerRepository = importCustomerRepository;
        this.userService = userService;
        this.customerService = customerService;
    }


    @Transactional
    public List<ImportCustomer> checkCsv(MultipartFile file) throws Exception {
        List<ImportCustomer> importCustomers = new ArrayList<>();
        List<String> errorLines = new ArrayList<>();

        // Lire le fichier CSV
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

        int lineNumber = 1;
        for (CSVRecord record : csvParser) {
            try {
                ImportCustomer importCustomer = new ImportCustomer();
                importCustomer.setCustomerEmail(record.get("customer_email"));
                importCustomer.setCustomerName(record.get("customer_name"));
                importCustomers.add(importCustomer);
            } catch (Exception e) {
                errorLines.add("Ligne " + lineNumber + " : " + e.getMessage());
            }
            lineNumber++;
        }

        // Si des erreurs sont survenues, les lister et les envoyer
        if (!errorLines.isEmpty()) {
            throw new Exception("Import failed at : " + errorLines);
        }
        return importCustomers;
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
