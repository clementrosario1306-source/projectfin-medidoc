package medidoc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.Doctor;

@Repository
public interface DoctorRepository extends MongoRepository<Doctor, String> {

    Optional<Doctor> findByDoctorId(String doctorId);

    boolean existsByDoctorId(String doctorId);

    List<Doctor> findByDepartment(String department);

    List<Doctor> findByStatus(Doctor.Status status);
}