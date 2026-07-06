package medidoc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByDoctorId(String doctorId);

    List<Doctor> findByDepartment(String department);

    List<Doctor> findByStatus(Doctor.Status status);

    boolean existsByDoctorId(String doctorId);
}