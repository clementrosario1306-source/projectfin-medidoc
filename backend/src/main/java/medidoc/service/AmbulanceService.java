package medidoc.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import medidoc.model.Ambulance;
import medidoc.model.AmbulanceRequest;
import medidoc.repository.AmbulanceRepository;
import medidoc.repository.AmbulanceRequestRepository;

@Service
public class AmbulanceService {

    @Autowired
    private AmbulanceRepository ambulanceRepository;

    @Autowired
    private AmbulanceRequestRepository ambulanceRequestRepository;

    // GET ALL AMBULANCES
    public List<Ambulance> getAllAmbulances() {
        return ambulanceRepository.findAll();
    }

    // GET AVAILABLE AMBULANCES
    public List<Ambulance> getAvailableAmbulances() {
        return ambulanceRepository.findByStatus(Ambulance.Status.AVAILABLE);
    }

    // REQUEST AMBULANCE
    public AmbulanceRequest requestAmbulance(AmbulanceRequest request) {
        request.setStatus(AmbulanceRequest.Status.REQUESTED);
        AmbulanceRequest saved = ambulanceRequestRepository.save(request);

        System.out.println("Ambulance requested for: "
            + request.getRequesterName() + " Priority: "
            + request.getPriority());
        return saved;
    }

    // ASSIGN AMBULANCE TO REQUEST
    public AmbulanceRequest assignAmbulance(String requestId, String ambulanceId) {

        Optional<AmbulanceRequest> requestOpt =
            ambulanceRequestRepository.findById(requestId);
        if (requestOpt.isEmpty()) {
            throw new RuntimeException("Request not found: " + requestId);
        }

        Optional<Ambulance> ambulanceOpt =
            ambulanceRepository.findById(ambulanceId);
        if (ambulanceOpt.isEmpty()) {
            throw new RuntimeException("Ambulance not found: " + ambulanceId);
        }

        Ambulance ambulance = ambulanceOpt.get();
        if (ambulance.getStatus() != Ambulance.Status.AVAILABLE) {
            throw new RuntimeException("Ambulance is not available");
        }

        AmbulanceRequest request = requestOpt.get();
        request.setAmbulanceId(ambulanceId);
        request.setStatus(AmbulanceRequest.Status.ASSIGNED);

        ambulance.setStatus(Ambulance.Status.ON_DUTY);
        ambulanceRepository.save(ambulance);

        return ambulanceRequestRepository.save(request);
    }

    // UPDATE AMBULANCE REQUEST STATUS
    public AmbulanceRequest updateRequestStatus(String requestId, String status) {

        Optional<AmbulanceRequest> requestOpt =
            ambulanceRequestRepository.findById(requestId);
        if (requestOpt.isEmpty()) {
            throw new RuntimeException("Request not found: " + requestId);
        }

        AmbulanceRequest request = requestOpt.get();
        request.setStatus(
            AmbulanceRequest.Status.valueOf(status.toUpperCase()));

        if (status.equalsIgnoreCase("COMPLETED")) {
            request.setCompletedAt(LocalDateTime.now());
            // Free up the ambulance
            if (request.getAmbulanceId() != null) {
                Optional<Ambulance> ambOpt =
                    ambulanceRepository.findById(request.getAmbulanceId());
                if (ambOpt.isPresent()) {
                    Ambulance amb = ambOpt.get();
                    amb.setStatus(Ambulance.Status.AVAILABLE);
                    ambulanceRepository.save(amb);
                }
            }
        }

        return ambulanceRequestRepository.save(request);
    }

    // GET PENDING REQUESTS
    public List<AmbulanceRequest> getPendingRequests() {
        return ambulanceRequestRepository
            .findByStatus(AmbulanceRequest.Status.REQUESTED);
    }
}
