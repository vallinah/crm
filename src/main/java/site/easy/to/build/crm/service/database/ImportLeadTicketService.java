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
import java.util.List;

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

    @Transactional
    public List<ImportLeadTicket> checkCsv(MultipartFile file) throws Exception {
        List<ImportLeadTicket> importLeadTickets = new ArrayList<>();
        List<String> errorLines = new ArrayList<>();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

        int lineNumber = 1;
        for (CSVRecord record : csvParser) {
            try {
                ImportLeadTicket importLeadTicket = new ImportLeadTicket();
                importLeadTicket.setCustomerEmail(record.get("customer_email"));
                importLeadTicket.setSubjectOrName(record.get("subject_or_name"));
                importLeadTicket.setType(record.get("type"));
                importLeadTicket.setStatus(record.get("status"));
                importLeadTicket.setAmount(parseAmount(record.get("expense"))); // Utilisation de BigDecimal
                importLeadTickets.add(importLeadTicket);
            } catch (Exception e) {
                errorLines.add("Ligne " + lineNumber + " : " + e.getMessage());
            }
            lineNumber++;
        }

        if (!errorLines.isEmpty()) {
            throw new Exception("Import failed at : " + errorLines);
        }
        return importLeadTickets;
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

                // TicketExpense ticketExpense = new TicketExpense();
                // ticketExpense.setTicketHisto(ticketHisto1);
                // ticketExpense.setAmount(expense);
                // ticketExpense.setCreatedAt(LocalDateTime.now());
                // ticketExpenseService.save(ticketExpense);

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
