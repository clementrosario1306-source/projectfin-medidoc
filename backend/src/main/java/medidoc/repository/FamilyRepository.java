package medidoc.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.FamilyMember;

@Repository
public interface FamilyRepository extends MongoRepository<FamilyMember, String> {

    List<FamilyMember> findByPrimaryPatientId(String primaryPatientId);
}
