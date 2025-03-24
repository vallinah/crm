package site.easy.to.build.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import site.easy.to.build.crm.entity.RateConfig;

public interface RateConfigRepository extends JpaRepository<RateConfig, Integer> {
    @Query("SELECT rc FROM RateConfig rc ORDER BY rc.createdAt DESC LIMIT 1")
    Optional<RateConfig> findLatest();
}
