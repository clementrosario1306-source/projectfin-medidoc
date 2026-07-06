package medidoc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.Alert;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByIsActiveTrue();

    List<Alert> findByAlertType(Alert.AlertType alertType);
}