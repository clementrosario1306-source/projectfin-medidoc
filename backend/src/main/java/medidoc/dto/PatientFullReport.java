package medidoc.dto;

import java.util.List;

import medidoc.model.Appointment;
import medidoc.model.MedicalHistory;
import medidoc.model.Patient;
import medidoc.model.Prescription;

public class PatientFullReport {

    private Patient patient;
    private List<MedicalHistory> medicalHistory;
    private List<Prescription> prescriptions;
    private List<Appointment> appointments;
    private String riskLevel;
    private String aiSummary;

    // Getters and Setters
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public List<MedicalHistory> getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(List<MedicalHistory> medicalHistory) { this.medicalHistory = medicalHistory; }

    public List<Prescription> getPrescriptions() { return prescriptions; }
    public void setPrescriptions(List<Prescription> prescriptions) { this.prescriptions = prescriptions; }

    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> appointments) { this.appointments = appointments; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
}
