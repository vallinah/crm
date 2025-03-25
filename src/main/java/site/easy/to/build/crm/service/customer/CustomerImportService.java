package site.easy.to.build.crm.service.customer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintViolation;
import site.easy.to.build.crm.dto.CustomerCsvDto;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.repository.CustomerRepository;
import site.easy.to.build.crm.repository.UserRepository;

@Service
public class CustomerImportService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final jakarta.validation.Validator validator;

    public CustomerImportService(CustomerRepository customerRepository,
            UserRepository userRepository,
            jakarta.validation.Validator validator) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.validator = validator;
    }

    @Transactional
    public ImportResult importCustomersFromCsv(MultipartFile file) throws ImportException {
        List<Customer> customersToSave = new ArrayList<>();
        List<LineError> errors = new ArrayList<>();
        String fileName = file.getOriginalFilename();

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
                CSVParser csvParser = new CSVParser(fileReader,
                        CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            for (CSVRecord csvRecord : csvParser) {
                try {
                    CustomerCsvDto dto = new CustomerCsvDto();
                    dto.setCustomerEmail(csvRecord.get("customer_email"));
                    dto.setCustomerName(csvRecord.get("customer_name"));

                    // Validation DTO
                    Set<ConstraintViolation<CustomerCsvDto>> violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        violations.forEach(v -> errors.add(
                                new LineError(
                                        fileName,
                                        csvRecord.getRecordNumber(),
                                        v.getPropertyPath().toString(),
                                        v.getMessage(),
                                        csvRecord.get("customer_email"),
                                        csvRecord.get("customer_name"))));
                        continue;
                    }

                    // Création et validation Customer
                    Customer customer = createCustomerWithGeneratedData(dto);
                    Set<ConstraintViolation<Customer>> jpaViolations = validator.validate(customer);
                    if (!jpaViolations.isEmpty()) {
                        jpaViolations.forEach(v -> errors.add(
                                new LineError(
                                        fileName,
                                        csvRecord.getRecordNumber(),
                                        v.getPropertyPath().toString(),
                                        v.getMessage(),
                                        customer.getEmail(),
                                        customer.getName())));
                        continue;
                    }

                    customersToSave.add(customer);

                } catch (Exception e) {
                    errors.add(new LineError(
                            fileName,
                            csvRecord.getRecordNumber(),
                            "Erreur de traitement",
                            e.getMessage(),
                            csvRecord.get("customer_email"),
                            csvRecord.get("customer_name")));
                }
            }

            if (!errors.isEmpty()) {
                throw new ImportException("Le fichier contient des erreurs - " + fileName, errors);
            }

            List<Customer> savedCustomers = customerRepository.saveAll(customersToSave);
            return new ImportResult(savedCustomers.size(), fileName);

        } catch (IOException e) {
            throw new ImportException("Erreur de lecture du fichier: " + fileName, Collections.emptyList());
        }
    }

    private Customer createCustomerWithGeneratedData(CustomerCsvDto dto) {
        Customer customer = new Customer();
        customer.setName(dto.getCustomerName());
        customer.setEmail(dto.getCustomerEmail());
        customer.setCreatedAt(LocalDateTime.now());

        // Génération des données manquantes
        customer.setCountry(generateRandomCountry());
        customer.setPhone(generateRandomPhone());
        customer.setAddress(generateRandomAddress());
        customer.setCity(generateRandomCity());
        customer.setState(generateRandomState());
        customer.setDescription("Client importé automatiquement");
        customer.setPosition("Non spécifié");

        // Pour les champs liés, on peut laisser null ou créer des valeurs factices
        customer.setUser(null); // ou créer un user fictif si nécessaire
        customer.setCustomerLoginInfo(null);

        return customer;
    }

    // Méthodes pour générer des données aléatoires
    private String generateRandomCountry() {
        String[] countries = { "France", "USA", "UK", "Germany", "Spain", "Italy" };
        return countries[new Random().nextInt(countries.length)];
    }

    private String generateRandomPhone() {
        return "+1-" + (1000000000 + new Random().nextInt(900000000));
    }

    private String generateRandomAddress() {
        String[] streets = { "Main St", "First Ave", "Park Blvd", "Oak Rd", "Pine Ln" };
        return (new Random().nextInt(999) + 1) + " " + streets[new Random().nextInt(streets.length)];
    }

    private String generateRandomCity() {
        String[] cities = { "Paris", "New York", "London", "Berlin", "Madrid", "Rome" };
        return cities[new Random().nextInt(cities.length)];
    }

    private String generateRandomState() {
        String[] states = { "Île-de-France", "California", "England", "Bavaria", "Catalonia", "Lazio" };
        return states[new Random().nextInt(states.length)];
    }

    // Classes internes pour le résultat
    public static class ImportResult {
        private final int savedCount;
        private final String fileName;

        public ImportResult(int savedCount, String fileName) {
            this.savedCount = savedCount;
            this.fileName = fileName;
        }

        public int getSavedCount() {
            return savedCount;
        }

        public String getFileName() {
            return fileName;
        }
    }

    public static class LineError {
        private final String fileName;
        private final long lineNumber;
        private final String field;
        private final String message;
        private final String email;
        private final String name;

        public LineError(String fileName, long lineNumber, String field, String message, String email, String name) {
            this.fileName = fileName;
            this.lineNumber = lineNumber;
            this.field = field;
            this.message = message;
            this.email = email;
            this.name = name;
        }

        // Getters
        public String getFileName() {
            return fileName;
        }

        public long getLineNumber() {
            return lineNumber;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }

        public String getEmail() {
            return email;
        }

        public String getName() {
            return name;
        }
    }

    public static class ImportException extends Exception {
        private final List<LineError> errors;

        public ImportException(String message, List<LineError> errors) {
            super(message);
            this.errors = errors;
        }

        public List<LineError> getErrors() {
            return errors;
        }
    }

    private <T> LineError createLineError(CSVRecord record, String fileName,
            ConstraintViolation<T> violation,
            String email, String name) {
        return new LineError(
                fileName,
                record.getRecordNumber(),
                violation.getPropertyPath().toString(),
                violation.getMessage(),
                email,
                name);
    }
}