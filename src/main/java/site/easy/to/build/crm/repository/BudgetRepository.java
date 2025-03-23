package site.easy.to.build.crm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import site.easy.to.build.crm.entity.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    public List<Budget> findByCustomerCustomerId(int customerId);

}
