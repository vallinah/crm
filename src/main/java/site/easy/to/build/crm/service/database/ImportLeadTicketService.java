package site.easy.to.build.crm.service.database;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import site.easy.to.build.crm.entity.*;
import site.easy.to.build.crm.repository.LeadExpenseRepository;
import site.easy.to.build.crm.repository.TicketExpenseRepository;
import site.easy.to.build.crm.repository.ImportLeadTicketRepository;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.lead.TriggerLeadHistoService;
import site.easy.to.build.crm.service.ticket.TicketHistoService;
import site.easy.to.build.crm.service.ticket.TicketService;
import site.easy.to.build.crm.service.user.UserService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class ImportLeadTicketService {
    private final ImportLeadTicketRepository importLeadTicketRepository;
    private final UserService userService;
    private final CustomerService customerService;
    private final LeadService leadService;
    private final TicketService ticketService;
    private final LeadExpenseRepository LeadExpenseRepository;
    private final TicketExpenseRepository ticketExpenseRepository;
    private final TriggerLeadHistoService triggerLeadHistoService;
    private final TicketHistoService ticketHistoService;

    public ImportLeadTicketService(ImportLeadTicketRepository importLeadTicketRepository, UserService userService,
            CustomerService customerService, LeadService leadService, TicketService ticketService,
            site.easy.to.build.crm.repository.LeadExpenseRepository leadExpenseRepository,
            TicketExpenseRepository ticketExpenseRepository, TriggerLeadHistoService triggerLeadHistoService,
            TicketHistoService ticketHistoService) {
        this.importLeadTicketRepository = importLeadTicketRepository;
        this.userService = userService;
        this.customerService = customerService;
        this.leadService = leadService;
        this.ticketService = ticketService;
        LeadExpenseRepository = leadExpenseRepository;
        this.ticketExpenseRepository = ticketExpenseRepository;
        this.triggerLeadHistoService = triggerLeadHistoService;
        this.ticketHistoService = ticketHistoService;
    }

    // @Transactional
    // public List<ImportLeadTicket> checkCsv(MultipartFile file) throws Exception {
    // List<ImportLeadTicket> importLeadTickets = new ArrayList<>();
    // List<String> errorLines = new ArrayList<>();
    // Set<String> existingEmails = new HashSet<>();

    // BufferedReader reader = new BufferedReader(
    // new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
    // CSVParser csvParser = new CSVParser(reader,
    // CSVFormat.DEFAULT.withFirstRecordAsHeader());

    // int lineNumber = 1;
    // for (CSVRecord record : csvParser) {
    // List<String> lineErrors = new ArrayList<>();
    // ImportLeadTicket importLeadTicket = new ImportLeadTicket();

    // // Validation de l'email
    // try {
    // String email = record.get("customer_email");
    // if (email == null || email.trim().isEmpty()) {
    // lineErrors.add("Email client manquant");
    // } else if (!isValidEmail(email)) {
    // lineErrors.add("Format email client invalide");
    // } else {
    // email = email.trim();
    // importLeadTicket.setCustomerEmail(email);

    // // Vérification que le customer existe
    // if (customerService.findByEmail(email) == null) {
    // lineErrors.add("Aucun client trouvé avec cet email");
    // }
    // }
    // } catch (Exception e) {
    // lineErrors.add("Erreur de lecture de l'email client");
    // }

    // // Validation du sujet/nom
    // try {
    // String subjectOrName = record.get("subject_or_name");
    // if (subjectOrName == null || subjectOrName.trim().isEmpty()) {
    // lineErrors.add("Sujet/Nom manquant");
    // } else if (subjectOrName.length() > 255) {
    // lineErrors.add("Sujet/Nom trop long (max 255 caractères)");
    // } else {
    // importLeadTicket.setSubjectOrName(subjectOrName.trim());
    // }
    // } catch (Exception e) {
    // lineErrors.add("Erreur de lecture du sujet/nom");
    // }

    // // Validation du type
    // try {
    // String type = record.get("type");
    // if (type == null || type.trim().isEmpty()) {
    // lineErrors.add("Type manquant");
    // } else if (!type.equalsIgnoreCase("lead") &&
    // !type.equalsIgnoreCase("ticket")) {
    // lineErrors.add("Type invalide (doit être 'lead' ou 'ticket')");
    // } else {
    // importLeadTicket.setType(type.trim().toLowerCase());
    // }
    // } catch (Exception e) {
    // lineErrors.add("Erreur de lecture du type");
    // }

    // // Validation du statut
    // try {
    // String status = record.get("status");
    // if (status == null || status.trim().isEmpty()) {
    // lineErrors.add("Statut manquant");
    // } else if (status.length() > 50) {
    // lineErrors.add("Statut trop long (max 50 caractères)");
    // } else {
    // importLeadTicket.setStatus(status.trim());
    // }
    // } catch (Exception e) {
    // lineErrors.add("Erreur de lecture du statut");
    // }

    // // Validation du montant
    // try {
    // String amountStr = record.get("expense");
    // if (amountStr == null || amountStr.trim().isEmpty()) {
    // lineErrors.add("Montant manquant");
    // } else {
    // BigDecimal amount = parseAmount(amountStr);
    // if (amount.compareTo(BigDecimal.ZERO) <= 0) {
    // lineErrors.add("Le montant doit être positif");
    // } else {
    // importLeadTicket.setAmount(amount);
    // }
    // }
    // } catch (Exception e) {
    // lineErrors.add("Erreur de montant: " + e.getMessage());
    // }

    // if (lineErrors.isEmpty()) {
    // importLeadTickets.add(importLeadTicket);
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
    // return importLeadTickets;
    // }

    @Transactional
    public List<ImportLeadTicket> checkCsv(MultipartFile file, List<ImportCustomer> importCustomers) throws Exception {
        List<ImportLeadTicket> importLeadTickets = new ArrayList<>();
        List<String> errorLines = new ArrayList<>();

        // Créer un set des emails valides (base + fichier customer)
        Set<String> validEmails = new HashSet<>();

        // Ajouter les emails existants en base
        customerService.findAll().forEach(c -> validEmails.add(c.getEmail().trim().toLowerCase()));

        // Ajouter les emails du fichier customer à importer
        if (importCustomers != null) {
            importCustomers.forEach(c -> validEmails.add(c.getCustomerEmail().trim().toLowerCase()));
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            int lineNumber = 1;
            for (CSVRecord record : csvParser) {
                List<String> lineErrors = new ArrayList<>();
                ImportLeadTicket importLeadTicket = new ImportLeadTicket();

                // Validation de l'email
                String email = record.get("customer_email");
                if (email == null || email.trim().isEmpty()) {
                    lineErrors.add("Email client manquant");
                } else if (!isValidEmail(email)) {
                    lineErrors.add("Format email client invalide");
                } else {
                    email = email.trim().toLowerCase();
                    importLeadTicket.setCustomerEmail(email);

                    if (!validEmails.contains(email)) {
                        lineErrors.add("Aucun client trouvé avec cet email (ni en base ni dans l'import)");
                    }
                }

                // Validation du sujet/nom
                String subjectOrName = record.get("subject_or_name");
                if (subjectOrName == null || subjectOrName.trim().isEmpty()) {
                    lineErrors.add("Sujet/Nom manquant");
                } else if (subjectOrName.length() > 255) {
                    lineErrors.add("Sujet/Nom trop long (max 255 caractères)");
                } else {
                    importLeadTicket.setSubjectOrName(subjectOrName.trim());
                }

                // Validation du type
                String type = record.get("type");
                if (type == null || type.trim().isEmpty()) {
                    lineErrors.add("Type manquant");
                } else if (!type.equalsIgnoreCase("lead") && !type.equalsIgnoreCase("ticket")) {
                    lineErrors.add("Type invalide (doit être 'lead' ou 'ticket')");
                } else {
                    importLeadTicket.setType(type.trim().toLowerCase());
                }

                // Validation du statut
                String status = record.get("status");
                if (status == null || status.trim().isEmpty()) {
                    lineErrors.add("Statut manquant");
                } else if (status.length() > 50) {
                    lineErrors.add("Statut trop long (max 50 caractères)");
                } else {
                    importLeadTicket.setStatus(status.trim());
                }

                // Validation du montant
                String amountStr = record.get("expense");
                if (amountStr == null || amountStr.trim().isEmpty()) {
                    lineErrors.add("Montant manquant");
                } else {
                    try {
                        BigDecimal amount = parseAmount(amountStr);
                        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                            lineErrors.add("Le montant doit être positif");
                        } else {
                            importLeadTicket.setAmount(amount);
                        }
                    } catch (Exception e) {
                        lineErrors.add("Erreur de montant: " + e.getMessage());
                    }
                }

                if (lineErrors.isEmpty()) {
                    importLeadTickets.add(importLeadTicket);
                } else {
                    errorLines.add("Ligne " + lineNumber + " : " + String.join(", ", lineErrors));
                }
                lineNumber++;
            }
        }

        if (!errorLines.isEmpty()) {
            throw new Exception("Échec de l'import Leads/Tickets : \n" + String.join("\n", errorLines));
        }
        return importLeadTickets;
    }

    private boolean isValidEmail(String email) {
        if (email == null)
            return false;

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email.trim()).matches();
    }

    public void importCsv(List<ImportLeadTicket> importLeadTickets) throws Exception {
        for (ImportLeadTicket importLeadTicket : importLeadTickets) {
            if (importLeadTicket.getType().equals("lead")) {
                Lead lead = new Lead();
                lead.setCustomer(customerService.findByEmail(importLeadTicket.getCustomerEmail()));
                lead.setEmployee(userService.findById(52));
                lead.setManager(userService.findById(52));
                lead.setName(importLeadTicket.getSubjectOrName());
                lead.setStatus(importLeadTicket.getStatus());
                lead.setCreatedAt(LocalDateTime.now());
                Lead leadcreated = leadService.save(lead);

                TriggerLeadHisto triggerLeadHisto = new TriggerLeadHisto();
                triggerLeadHisto.setId(leadcreated.getLeadId());
                triggerLeadHisto.setCustomer(leadcreated.getCustomer());
                triggerLeadHisto.setUser(leadcreated.getManager());
                triggerLeadHisto.setName(leadcreated.getName());
                triggerLeadHisto.setPhone(leadcreated.getPhone());
                triggerLeadHisto.setEmployee(leadcreated.getEmployee());
                triggerLeadHisto.setStatus(leadcreated.getStatus());
                triggerLeadHisto.setMeetingId(leadcreated.getMeetingId());
                triggerLeadHisto.setGoogleDrive(leadcreated.getGoogleDrive());
                triggerLeadHisto.setGoogleDriveFolderId(leadcreated.getGoogleDriveFolderId());
                triggerLeadHisto.setCreatedAt(leadcreated.getCreatedAt());
                triggerLeadHisto.setDeleteAt(null);

                TriggerLeadHisto createTriggerLeadHisto = triggerLeadHistoService.save(triggerLeadHisto);

                LeadExpense depensesLead = new LeadExpense();
                depensesLead.setCreatedAt(LocalDateTime.now());
                depensesLead.setTriggerLeadHisto(createTriggerLeadHisto);
                depensesLead.setAmount(importLeadTicket.getAmount());
                LeadExpenseRepository.save(depensesLead);
            } else {
                Ticket ticket = new Ticket();
                ticket.setCustomer(customerService.findByEmail(importLeadTicket.getCustomerEmail()));
                ticket.setEmployee(userService.findById(52));
                ticket.setManager(userService.findById(52));
                ticket.setSubject(importLeadTicket.getSubjectOrName());
                ticket.setStatus(importLeadTicket.getStatus());
                ticket.setCreatedAt(LocalDateTime.now());
                ticket.setPriority("low");
                Ticket ticketcreated = ticketService.save(ticket);

                TicketHisto ticketHisto = Ticket.convertToTicketHisto(ticketcreated);
                TicketHisto ticketHisto1 = ticketHistoService.save(ticketHisto);

                TicketExpense depensesTicket = new TicketExpense();
                depensesTicket.setCreatedAt(LocalDateTime.now());
                depensesTicket.setTicketHisto(ticketHisto1);
                depensesTicket.setAmount(importLeadTicket.getAmount());
                ticketExpenseRepository.save(depensesTicket);
            }
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
