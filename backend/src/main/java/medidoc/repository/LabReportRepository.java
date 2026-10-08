package medidoc.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.LabReport;

@Repository
public interface LabReportRepository extends MongoRepository<LabReport, String> {

    List<LabReport> findByPatientIdOrderByTestDateDesc(String patientId);
}
