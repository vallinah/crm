package site.easy.to.build.crm.service.lead;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import site.easy.to.build.crm.entity.TriggerLeadHisto;
import site.easy.to.build.crm.repository.TriggerLeadHistoRepository;

@Service
@Transactional
public class TriggerLeadHistoServiceImpl implements TriggerLeadHistoService {
    private final TriggerLeadHistoRepository triggerLeadHistoRepository;

    public TriggerLeadHistoServiceImpl(TriggerLeadHistoRepository triggerLeadHistoRepository) {
        this.triggerLeadHistoRepository = triggerLeadHistoRepository;
    }

    @Override
    public TriggerLeadHisto save(TriggerLeadHisto triggerLeadHisto) {
        return triggerLeadHistoRepository.save(triggerLeadHisto);
    }

    @Override
    public List<TriggerLeadHisto> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return triggerLeadHistoRepository.findByCreatedAtBetween(startDate, endDate);
    }

    @Override
    public TriggerLeadHisto getById(Integer id) {
        return triggerLeadHistoRepository.findByIdAndDeleteAtIsNull(id);
    }

    @Override
    public List<TriggerLeadHisto> getAll() {
        return triggerLeadHistoRepository.findByDeleteAtIsNull();
    }

    @Override
    public TriggerLeadHisto update(Integer id, TriggerLeadHisto triggerLeadHisto) {
        TriggerLeadHisto existingTriggerLeadHisto = triggerLeadHistoRepository.findByIdAndDeleteAtIsNull(id);
        if (existingTriggerLeadHisto == null) {
            throw new RuntimeException("TriggerLeadHisto non trouvé avec l'ID : " + id);
        }
        existingTriggerLeadHisto.setName(triggerLeadHisto.getName());
        existingTriggerLeadHisto.setPhone(triggerLeadHisto.getPhone());
        existingTriggerLeadHisto.setStatus(triggerLeadHisto.getStatus());
        existingTriggerLeadHisto.setMeetingId(triggerLeadHisto.getMeetingId());
        existingTriggerLeadHisto.setGoogleDrive(triggerLeadHisto.getGoogleDrive());
        existingTriggerLeadHisto.setGoogleDriveFolderId(triggerLeadHisto.getGoogleDriveFolderId());

        return triggerLeadHistoRepository.save(existingTriggerLeadHisto);
    }

    @Override
    public void delete(Integer id) {
        TriggerLeadHisto triggerLeadHisto = triggerLeadHistoRepository.findByIdAndDeleteAtIsNull(id);
        if (triggerLeadHisto == null) {
            throw new RuntimeException("TriggerLeadHisto non trouvé avec l'ID : " + id);
        }
        triggerLeadHisto.setDeleteAt(LocalDateTime.now());
        triggerLeadHistoRepository.save(triggerLeadHisto);
    }

    @Override
    public void softDelete(Integer id) {
        triggerLeadHistoRepository.markAsDeletedNow(id);
    }

    @Override
    public List<TriggerLeadHisto> getTriggerLeadHistoBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return triggerLeadHistoRepository.getBetweenDate(startDate, endDate);
    }
}
