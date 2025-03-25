package site.easy.to.build.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import site.easy.to.build.crm.entity.LeadExpense;

@Repository
public interface LeadExpenseRepository extends JpaRepository<LeadExpense, Long> {

    @Query(value = "SELECT * FROM lead_expense WHERE trigger_lead_histo_id = :triggerLeadHistoId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    LeadExpense findLatestByTriggerLeadHistoId(@Param("triggerLeadHistoId") Integer triggerLeadHistoId);

    Optional<LeadExpense> findById(Integer id);
}
