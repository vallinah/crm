package site.easy.to.build.crm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "import_lead_ticket")
public class ImportLeadTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "customer_email" , nullable = false, length = 100)
    private String customerEmail;

    @Column( name = "subject_or_name" , nullable = false, length = 100)
    private String subjectOrName;

    @Column( name = "type" ,nullable = false, length = 100)
    private String type;

    @Column( name = "status" ,nullable = false)
    private String status;


    @Column( name = "amount" ,nullable = false)
    private BigDecimal amount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getSubjectOrName() {
        return subjectOrName;
    }

    public void setSubjectOrName(String subjectOrName) {
        this.subjectOrName = subjectOrName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type == null || (!type.equals("lead") && !type.equals("ticket"))) {
            throw new IllegalArgumentException("Le type doit être soit 'lead', soit 'ticket'");
        }
        this.type = type;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if ("lead".equals(this.getType())) {
            // Vérifie si le type est "lead", les statuts possibles sont limités aux valeurs suivantes
            if (!status.equals("meeting-to-schedule") &&
                    !status.equals("assign-to-sales") &&
                    !status.equals("archived") &&
                    !status.equals("success")) {
                throw new IllegalArgumentException("Le statut "+ status +" est invalide pour 'lead' ");            }
        } else if ("ticket".equals(this.getType())) {
            // Vérifie si le type est "ticket", les statuts possibles sont limités aux valeurs suivantes
            if (!status.equals("open") &&
                    !status.equals("assigned") &&
                    !status.equals("on-hold") &&
                    !status.equals("in-progress") &&
                    !status.equals("resolved") &&
                    !status.equals("closed") &&
                    !status.equals("reopened") &&
                    !status.equals("pending-customer-response") &&
                    !status.equals("escalated") &&
                    !status.equals("archived")) {
                throw new IllegalArgumentException("Le statut "+ status +" est invalide pour 'ticket' ");
            }
        } else {
            throw new IllegalStateException("Le type est invalide, il doit être soit 'lead' soit 'ticket'.");
        }

        // Si aucune exception n'a été lancée, le statut est valide et peut être défini
        this.status = status;
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
    }


}


