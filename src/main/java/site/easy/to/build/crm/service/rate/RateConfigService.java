package site.easy.to.build.crm.service.rate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import site.easy.to.build.crm.entity.RateConfig;
import site.easy.to.build.crm.repository.RateConfigRepository;

@Service
public class RateConfigService {
    private final RateConfigRepository rateConfigRepository;

    public RateConfigService(RateConfigRepository rateConfigRepository) {
        this.rateConfigRepository = rateConfigRepository;
    }

    public Optional<RateConfig> findLatest() {
        return rateConfigRepository.findLatest();
    }

    public RateConfig save(RateConfig rateConfig) {
        return rateConfigRepository.save(rateConfig);
    }

    public List<RateConfig> findBetweenDates(LocalDate startDate, LocalDate endDate) {
        return rateConfigRepository.findRateConfigsBetweenDates(startDate, endDate);
    }
}
