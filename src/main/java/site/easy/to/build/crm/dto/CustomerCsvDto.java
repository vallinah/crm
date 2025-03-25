package site.easy.to.build.crm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CustomerCsvDto {

    @NotBlank(message = "L'email du client est obligatoire")
    @Email(message = "L'email doit être valide")
    private String customerEmail;

    @NotBlank(message = "Le nom du client est obligatoire")
    private String customerName;

    // Getters and Setters
    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
