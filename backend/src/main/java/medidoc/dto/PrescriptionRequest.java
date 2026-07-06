package medidoc.dto;

import java.util.List;

public class PrescriptionRequest {

    private String patientUniqueId;
    private String doctorId;
    private Long historyId;
    private String notes;
    private List<MedicineItem> medicines;

    public static class MedicineItem {
        private Long medicineId;
        private String dosage;
        private String frequency;
        private Integer durationDays;
        private String instructions;

        public Long getMedicineId() { return medicineId; }
        public void setMedicineId(Long medicineId) { this.medicineId = medicineId; }

        public String getDosage() { return dosage; }
        public void setDosage(String dosage) { this.dosage = dosage; }

        public String getFrequency() { return frequency; }
        public void setFrequency(String frequency) { this.frequency = frequency; }

        public Integer getDurationDays() { return durationDays; }
        public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }

        public String getInstructions() { return instructions; }
        public void setInstructions(String instructions) { this.instructions = instructions; }
    }

    // Getters and Setters
    public String getPatientUniqueId() { return patientUniqueId; }
    public void setPatientUniqueId(String patientUniqueId) { this.patientUniqueId = patientUniqueId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public Long getHistoryId() { return historyId; }
    public void setHistoryId(Long historyId) { this.historyId = historyId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<MedicineItem> getMedicines() { return medicines; }
    public void setMedicines(List<MedicineItem> medicines) { this.medicines = medicines; }
}