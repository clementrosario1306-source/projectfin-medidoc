package medidoc.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.Alert;

@Repository
public interface AlertRepository extends MongoRepository<Alert, String> {

    List<Alert> findByIsActiveTrue();

    List<Alert> findByIsActiveTrueAndSeverity(Alert.Severity severity);

    List<Alert> findByAlertType(Alert.AlertType alertType);
}