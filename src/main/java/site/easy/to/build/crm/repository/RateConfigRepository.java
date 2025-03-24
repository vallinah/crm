package site.easy.to.build.crm.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.easy.to.build.crm.entity.RateConfig;

public interface RateConfigRepository extends JpaRepository<RateConfig, Integer> {
    @Query("SELECT rc FROM RateConfig rc ORDER BY rc.createdAt DESC LIMIT 1")
    Optional<RateConfig> findLatest();

    @Query("SELECT r FROM RateConfig r " +
            "WHERE (:startDate IS NULL OR r.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR r.createdAt <= :endDate) " +
            "ORDER BY r.createdAt DESC")
    List<RateConfig> findRateConfigsBetweenDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
