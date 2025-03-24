package site.easy.to.build.crm.service.rate;

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
}
