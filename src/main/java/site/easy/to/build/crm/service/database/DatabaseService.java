package site.easy.to.build.crm.service.database;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class DatabaseService {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void resetDatabase() {
        // Désactiver les contraintes de clé étrangère
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0;").executeUpdate();

        // Tronquer les tables (sauf `users`)
        entityManager.createNativeQuery("TRUNCATE TABLE employee;").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE email_template;").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE customer_login_info;").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE customer;").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE trigger_lead;").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE trigger_ticket;").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE trigger_contract;").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE contract_settings;").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE lead_action;").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE lead_settings;").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE ticket_settings;").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE file;").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE google_drive_file;").executeUpdate();

        // Réactiver les contraintes de clé étrangère
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1;").executeUpdate();
    }
}
