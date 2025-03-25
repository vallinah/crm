package site.easy.to.build.crm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LeadExpenseUpdateDto {
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private Integer leadId;

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

    public Integer getLeadId() {
        return leadId;
    }

    public void setLeadId(Integer leadId) {
        this.leadId = leadId;
    }
}
