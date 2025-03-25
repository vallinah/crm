package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.easy.to.build.crm.entity.ImportBudgetCustomer;

public interface ImportBudgetCustomerRepository extends JpaRepository<ImportBudgetCustomer, Integer> {
}
