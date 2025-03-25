package site.easy.to.build.crm.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.easy.to.build.crm.entity.TriggerLeadHisto;

public interface TriggerLeadHistoRepository extends JpaRepository<TriggerLeadHisto, Long> {
    List<TriggerLeadHisto> findByCustomerCustomerId(int idCustomer);

    List<TriggerLeadHisto> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<TriggerLeadHisto> findByDeleteAtIsNull();

    TriggerLeadHisto findByIdAndDeleteAtIsNull(Integer id);

    @Modifying
    @Query("UPDATE TriggerLeadHisto t SET t.deleteAt = :now WHERE t.id = :id")
    void updateDeletedAt(@Param("id") Integer id, @Param("now") LocalDateTime now);

    // Méthode pratique qui utilise directement LocalDateTime.now()
    default void markAsDeletedNow(Integer id) {
        updateDeletedAt(id, LocalDateTime.now());
    }
}