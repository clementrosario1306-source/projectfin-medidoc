package medidoc.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.Staff;

@Repository
public interface StaffRepository extends MongoRepository<Staff, String> {

    Optional<Staff> findByEmployeeId(String employeeId);
}
