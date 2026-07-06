package medidoc.controller;

import medidoc.dto.ApiResponse;
import medidoc.model.FamilyMember;
import medidoc.service.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/family")
@CrossOrigin(origins = "*")
public class FamilyController {

    @Autowired
    private FamilyService familyService;

    @PostMapping("/link")
    public ResponseEntity<ApiResponse> linkFamilyMember(
            @RequestParam String primaryUniqueId,
            @RequestParam String memberUniqueId,
            @RequestParam String relationship) {
        try {
            FamilyMember family = familyService.linkFamilyMember(
                primaryUniqueId, memberUniqueId, relationship);
            return ResponseEntity.ok(ApiResponse.success(
                "Family member linked successfully", family));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{uniqueId}")
    public ResponseEntity<ApiResponse> getFamilyMembers(
            @PathVariable String uniqueId) {
        try {
            List<FamilyMember> members =
                familyService.getFamilyMembers(uniqueId);
            return ResponseEntity.ok(ApiResponse.success(
                "Family members: " + members.size(), members));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}
