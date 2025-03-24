package site.easy.to.build.crm.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "trigger_lead_histo")
public class TriggerLeadHisto {

    @Id
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "phone", length = 20)
    private String phone;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User employee;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "meeting_id", length = 255, unique = true)
    private String meetingId;

    @Column(name = "google_drive")
    private Boolean googleDrive;

    @Column(name = "google_drive_folder_id", length = 255)
    private String googleDriveFolderId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;

    // Constructeur par défaut (obligatoire pour JPA)
    public TriggerLeadHisto() {
    }

    // Constructeur avec tous les champs (optionnel)
    public TriggerLeadHisto(Integer id, Customer customer, User user, String name, String phone, User employee,
            String status, String meetingId, Boolean googleDrive, String googleDriveFolderId,
            LocalDateTime createAt, LocalDateTime deleteAt) {
        this.id = id;
        this.customer = customer;
        this.user = user;
        this.name = name;
        this.phone = phone;
        this.employee = employee;
        this.status = status;
        this.meetingId = meetingId;
        this.googleDrive = googleDrive;
        this.googleDriveFolderId = googleDriveFolderId;
        this.createdAt = createAt;
        this.deleteAt = deleteAt;
    }

    // Getters et setters pour chaque champ
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public Boolean getGoogleDrive() {
        return googleDrive;
    }

    public void setGoogleDrive(Boolean googleDrive) {
        this.googleDrive = googleDrive;
    }

    public String getGoogleDriveFolderId() {
        return googleDriveFolderId;
    }

    public void setGoogleDriveFolderId(String googleDriveFolderId) {
        this.googleDriveFolderId = googleDriveFolderId;
    }

    public LocalDateTime getDeleteAt() {
        return deleteAt;
    }

    public void setDeleteAt(LocalDateTime deleteAt) {
        this.deleteAt = deleteAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}