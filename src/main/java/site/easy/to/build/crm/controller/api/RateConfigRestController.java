package site.easy.to.build.crm.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.entity.RateConfig;
import site.easy.to.build.crm.repository.RateConfigRepository;
import site.easy.to.build.crm.service.rate.RateConfigService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rate-configs")
public class RateConfigRestController {

    private final RateConfigService rateConfigService;

    @Autowired
    public RateConfigRestController(RateConfigService rateConfigService) {
        this.rateConfigService = rateConfigService;
    }

    // Endpoint pour créer une nouvelle configuration de taux
    @PostMapping
    public ResponseEntity<RateConfig> createRateConfig(@RequestBody RateConfig rateConfig) {
        // Si la date n'est pas fournie, on utilise la date actuelle
        if (rateConfig.getCreatedAt() == null) {
            rateConfig.setCreatedAt(LocalDate.now());
        }

        RateConfig savedRateConfig = rateConfigService.save(rateConfig);
        return new ResponseEntity<>(savedRateConfig, HttpStatus.CREATED);
    }

    // Endpoint pour récupérer toutes les configurations de taux
    @GetMapping
    public ResponseEntity<List<RateConfig>> getAllRateConfigs(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        List<RateConfig> rateConfigs = rateConfigService.findBetweenDates(startDate, endDate);
        return new ResponseEntity<>(rateConfigs, HttpStatus.OK);
    }
}
