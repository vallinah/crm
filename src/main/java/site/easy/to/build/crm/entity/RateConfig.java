package site.easy.to.build.crm.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "rate_config")
public class RateConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int budgetId;

    @Column(name = "rate", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Rate is required")
    @Digits(integer = 10, fraction = 2, message = "Rate must be a valid number with up to 2 decimal places")
    @DecimalMin(value = "0.00", inclusive = true, message = "Rate must be greater than or equal to 0.00")
    @DecimalMax(value = "9999999.99", inclusive = true, message = "Rate must be less than or equal to 9999999.99")
    private BigDecimal rate;

    @Column(name = "created_at")
    @NotNull(message = "Date is required")
    private LocalDate createdAt;

    public RateConfig(int budgetId,
            @NotNull(message = "Rate is required") @Digits(integer = 10, fraction = 2, message = "Rate must be a valid number with up to 2 decimal places") @DecimalMin(value = "0.00", inclusive = true, message = "Rate must be greater than or equal to 0.00") @DecimalMax(value = "9999999.99", inclusive = true, message = "Rate must be less than or equal to 9999999.99") BigDecimal rate,
            @NotNull(message = "Date is required") LocalDate createdAt) {
        this.budgetId = budgetId;
        this.rate = rate;
        this.createdAt = createdAt;
    }

    public RateConfig() {
    }

    public int getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
