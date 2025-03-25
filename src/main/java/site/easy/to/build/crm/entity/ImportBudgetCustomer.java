package site.easy.to.build.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import site.easy.to.build.crm.customValidations.customer.UniqueEmail;

import java.math.BigDecimal;

@Entity
public class ImportBudgetCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "customer_email")
    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email format")
    private String customerEmail;
    @Column( name = "amount" ,nullable = false)
    private BigDecimal amount;

    // Getters et setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
    }
}
