package site.easy.to.build.crm.repository;

import site.easy.to.build.crm.entity.*;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TriggerLeadHistoRepository extends JpaRepository<TriggerLeadHisto, Long> {
    List<TriggerLeadHisto> findByCustomerCustomerId(int idCustomer);
}