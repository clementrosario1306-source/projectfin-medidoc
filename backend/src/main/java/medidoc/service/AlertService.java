package medidoc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import medidoc.model.Alert;
import medidoc.repository.AlertRepository;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    // BROADCAST NEW ALERT
    public Alert broadcastAlert(Alert alert) {
        alert.setIsActive(true);
        Alert saved = alertRepository.save(alert);
        System.out.println("Alert broadcast: " + alert.getTitle()
            + " Severity: " + alert.getSeverity());
        return saved;
    }

    // GET ALL ACTIVE ALERTS
    public List<Alert> getActiveAlerts() {
        return alertRepository.findByIsActiveTrue();
    }

    // GET ALL ALERTS
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    // DEACTIVATE ALERT
    public Alert deactivateAlert(String id) {
        Optional<Alert> alertOpt = alertRepository.findById(id);
        if (alertOpt.isEmpty()) {
            throw new RuntimeException("Alert not found: " + id);
        }
        Alert alert = alertOpt.get();
        alert.setIsActive(false);
        return alertRepository.save(alert);
    }

    // GET ALERTS BY TYPE
    public List<Alert> getAlertsByType(String type) {
        return alertRepository.findByAlertType(
            Alert.AlertType.valueOf(type.toUpperCase()));
    }
}
