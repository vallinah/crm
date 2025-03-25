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
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.repository.CustomerRepository;
import site.easy.to.build.crm.repository.UserRepository;

@Service
public class CustomerImportService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final jakarta.validation.Validator validator;
    private final Random random = new Random();

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

        List<User> availableUsers = userRepository.findAll();
        if (availableUsers.isEmpty()) {
            throw new ImportException("Aucun utilisateur disponible dans la base de données", Collections.emptyList());
        }

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

                    // Sélection aléatoire d'un utilisateur
                    User randomUser = availableUsers.get(random.nextInt(availableUsers.size()));

                    // Création du client
                    Customer customer = createCustomerWithDefaults(dto, randomUser);

                    // Validation JPA
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

    private Customer createCustomerWithDefaults(CustomerCsvDto dto, User user) {
        Customer customer = new Customer();
        customer.setName(dto.getCustomerName());
        customer.setEmail(dto.getCustomerEmail());
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUser(user);

        // Valeurs par défaut
        customer.setCountry(generateRandomCountry());
        customer.setPhone(generateRandomPhone());
        customer.setAddress(generateRandomAddress());
        customer.setCity(generateRandomCity());
        customer.setState(generateRandomState());
        customer.setDescription("Client importé");
        customer.setPosition("Non spécifié");

        return customer;
    }

    private String generateRandomCountry() {
        String[] countries = { "France", "USA", "UK", "Germany", "Spain", "Italy" };
        return countries[new Random().nextInt(countries.length)];
    }

    // Méthodes pour générer des données aléatoires
    private String generateRandomPhone() {
        return "+33" + (100000000 + random.nextInt(900000000));
    }

    private String generateRandomAddress() {
        String[] streets = { "Rue de la Paix", "Avenue des Champs-Élysées", "Boulevard Saint-Germain" };
        return (random.nextInt(100) + 1) + " " + streets[random.nextInt(streets.length)];
    }

    private String generateRandomCity() {
        String[] cities = { "Paris", "Lyon", "Marseille", "Toulouse", "Nice" };
        return cities[random.nextInt(cities.length)];
    }

    private String generateRandomState() {
        String[] states = { "Île-de-France", "Auvergne-Rhône-Alpes", "Provence-Alpes-Côte d'Azur" };
        return states[random.nextInt(states.length)];
    }

    // Classes internes
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
}