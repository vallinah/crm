package site.easy.to.build.crm.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "import_customer")
public class ImportCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;  // Utilisation de int pour l'id

    @Column(name = "customer_email", nullable = false, length = 100)
    private String customerEmail;

    @Column(name = "customer_name", nullable = false, length = 50)
    private String customerName;

    // Constructeurs
    public ImportCustomer() {}

    public ImportCustomer(String customerEmail, String customerName) {
        this.customerEmail = customerEmail;
        this.customerName = customerName;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    @Override
    public String toString() {
        return "ImportCustomer{id=" + id + ", customerEmail='" + customerEmail + "', customerName='" + customerName + "'}";
    }
}
