package site.easy.to.build.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lead_expense")
public class LeadExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    @NotNull(message = "Amount is required")
    @Digits(integer = 10, fraction = 2, message = "Amount must be a valid number with up to 2 decimal places")
    @DecimalMin(value = "0.00", inclusive = true, message = "Amount must be greater than or equal to 0.00")
    @DecimalMax(value = "9999999.99", inclusive = true, message = "Amount must be less than or equal to 9999999.99")
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "trigger_lead_histo_id", nullable = false)
    private TriggerLeadHisto triggerLeadHisto;

    public LeadExpense() {
    }

    public LeadExpense(
            @NotNull(message = "Amount is required") @Digits(integer = 10, fraction = 2, message = "Amount must be a valid number with up to 2 decimal places") @DecimalMin(value = "0.00", inclusive = true, message = "Amount must be greater than or equal to 0.00") @DecimalMax(value = "9999999.99", inclusive = true, message = "Amount must be less than or equal to 9999999.99") BigDecimal amount,
            LocalDateTime createdAt, TriggerLeadHisto triggerLeadHisto) {
        this.amount = amount;
        this.createdAt = createdAt;
        this.triggerLeadHisto = triggerLeadHisto;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public TriggerLeadHisto getTriggerLeadHisto() {
        return triggerLeadHisto;
    }

    public void setTriggerLeadHisto(TriggerLeadHisto triggerLeadHisto) {
        this.triggerLeadHisto = triggerLeadHisto;
    }
}