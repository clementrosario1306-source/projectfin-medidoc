/* ==========================================================
   MediDoc - doctor.js
   All logic for doctor-dashboard.html
   ========================================================== */

let currentReportPatient = null;  // raw Patient object from the report
let medicineRowCount = 0;

/* ===================== INIT ===================== */

document.addEventListener("DOMContentLoaded", async () => {
  const session = requireAuth("DOCTOR");
  if (!session) return;

  renderTodayDate();
  document.getElementById("doctor-avatar").textContent = getInitials(session.fullName);
  document.getElementById("page-title").textContent = "Dr. " + session.fullName.split(" ").pop() + " - Search Patient";

  await loadTodayQueue(session.uniqueId);
  addMedicineRow(); // start prescription modal with one empty row
});

/* ===================== SECTION SWITCHING ===================== */

function showSection(name) {
  document.querySelectorAll(".section").forEach(s => s.classList.remove("active"));
  document.querySelectorAll(".smenu-item").forEach(m => m.classList.remove("active"));

  document.getElementById("section-" + name).classList.add("active");
  document.querySelector(`.smenu-item[data-section="${name}"]`).classList.add("active");

  const titles = { search: "Search Patient", queue: "Today's Queue" };
  document.getElementById("page-title").textContent = titles[name] || "MediDoc";
}

/* ===================== REPORT TAB SWITCHING ===================== */

function switchReportTab(name) {
  document.querySelectorAll(".rtab").forEach(t => t.classList.remove("active"));
  document.querySelectorAll(".report-tab-content").forEach(c => c.classList.remove("active"));
  document.querySelector(`.rtab[data-tab="${name}"]`).classList.add("active");
  document.getElementById("tab-" + name).classList.add("active");
}

/* ===================== SEARCH PATIENT - THE MAIN FEATURE ===================== */

async function searchPatient() {
  clearMessage("search-msg");
  document.getElementById("report-card").style.display = "none";

  const uniqueId = document.getElementById("search-unique-id").value.trim();
  if (!uniqueId) {
    showMessage("search-msg", "Please enter a Patient Unique ID.", "error");
    return;
  }

  showLoading();
  const res = await apiGet(`/api/patients/${uniqueId}/full-report`);
  hideLoading();

  if (!res.data.success) {
    showMessage("search-msg", res.data.message || "Patient not found.", "error");
    return;
  }

  renderFullReport(res.data.data);
  document.getElementById("report-card").style.display = "block";
}

function renderFullReport(report) {
  const p = report.patient;
  currentReportPatient = p;

  // --- Header ---
  document.getElementById("report-avatar").textContent = getInitials(p.fullName);
  document.getElementById("report-name").textContent = p.fullName;
  document.getElementById("report-meta").textContent =
    `${p.uniqueId} \u00b7 Registered: ${formatDate(p.registrationDate)}`;

  const riskClass = riskTagClass(report.riskLevel);
  document.getElementById("report-tags").innerHTML = `
    <span class="tag tag-blood">${formatBloodGroup(p.bloodGroup)}</span>
    <span class="tag ${riskClass}">Risk: ${humanizeStatus(report.riskLevel || "LOW")}</span>
  `;

  // --- Allergy warning (placeholder, since allergy field isn't on Patient model yet) ---
  // Shown only if backend later adds an allergies field; kept defensive so nothing breaks.
  const allergyBox = document.getElementById("allergy-warning");
  if (p.allergies) {
    allergyBox.style.display = "block";
    allergyBox.textContent = "Allergy Warning: " + p.allergies + " - Do NOT prescribe";
  } else {
    allergyBox.style.display = "none";
  }

  // --- Personal Info tab ---
  document.getElementById("info-grid").innerHTML = `
    <div class="pfield"><div class="pfield-label">Age / Gender</div><div class="pfield-value">${p.age} / ${humanizeStatus(p.gender)}</div></div>
    <div class="pfield"><div class="pfield-label">Mobile</div><div class="pfield-value">${p.mobileNumber || "--"}</div></div>
    <div class="pfield"><div class="pfield-label">Blood Group</div><div class="pfield-value">${formatBloodGroup(p.bloodGroup)}</div></div>
    <div class="pfield"><div class="pfield-label">Address</div><div class="pfield-value">${p.address || "--"}</div></div>
    <div class="pfield"><div class="pfield-label">ABHA No.</div><div class="pfield-value">${p.abhaNumber || "Not linked"}</div></div>
    <div class="pfield"><div class="pfield-label">PM-JAY</div><div class="pfield-value">${p.isPmjayEligible ? '<span class="status-pill pill-green">Eligible</span>' : '<span class="status-pill pill-gray">Not eligible</span>'}</div></div>
  `;

  // --- Medical History tab (THE disease/diagnosis history) ---
  const historyBody = document.querySelector("#history-table tbody");
  if (!report.medicalHistory || report.medicalHistory.length === 0) {
    historyBody.innerHTML = `<tr><td colspan="5" class="empty-row">No medical history recorded yet.</td></tr>`;
  } else {
    historyBody.innerHTML = report.medicalHistory.map(h => `
      <tr>
        <td>${formatDate(h.visitDate)}</td>
        <td>${h.chiefComplaint || "--"}</td>
        <td><strong>${h.diagnosis}</strong></td>
        <td>${h.notes || "--"}</td>
        <td>${h.followUpDate ? formatDate(h.followUpDate) : "--"}</td>
      </tr>
    `).join("");
  }

  // --- Prescriptions tab ---
  const rxBody = document.querySelector("#rx-table tbody");
  if (!report.prescriptions || report.prescriptions.length === 0) {
    rxBody.innerHTML = `<tr><td colspan="3" class="empty-row">No prescriptions yet.</td></tr>`;
  } else {
    rxBody.innerHTML = report.prescriptions.map(rx => `
      <tr>
        <td>${formatDate(rx.prescribedDate)}</td>
        <td>${rx.notes || "--"}</td>
        <td><span class="status-pill ${statusPillClass(rx.status)}">${humanizeStatus(rx.status)}</span></td>
      </tr>
    `).join("");
  }

  // --- Lab Reports tab (fetched separately since not part of full-report yet) ---
  loadPatientLabReports(p.uniqueId);

  // --- Appointments tab ---
  const apptBody = document.querySelector("#appts-table tbody");
  if (!report.appointments || report.appointments.length === 0) {
    apptBody.innerHTML = `<tr><td colspan="4" class="empty-row">No appointments yet.</td></tr>`;
  } else {
    apptBody.innerHTML = report.appointments.map(a => `
      <tr>
        <td>#${String(a.tokenNumber).padStart(3, "0")}</td>
        <td>${formatDate(a.appointmentDate)}</td>
        <td>${a.timeSlot || "--"}</td>
        <td><span class="status-pill ${statusPillClass(a.status)}">${humanizeStatus(a.status)}</span></td>
      </tr>
    `).join("");
  }

  // --- AI Summary tab ---
  document.getElementById("ai-summary-text").textContent =
    report.aiSummary || "No AI summary available for this patient yet.";

  // Reset to first tab each time a new patient is loaded
  switchReportTab("info");
}

async function loadPatientLabReports(uniqueId) {
  const res = await apiGet(`/api/lab-reports/patient/${uniqueId}`);
  const body = document.querySelector("#lab-table tbody");

  if (!res.data.success || !res.data.data || res.data.data.length === 0) {
    body.innerHTML = `<tr><td colspan="5" class="empty-row">No lab reports yet.</td></tr>`;
    return;
  }

  body.innerHTML = res.data.data.map(lr => `
    <tr>
      <td>${lr.testName}</td>
      <td>${formatDate(lr.testDate)}</td>
      <td>${lr.result || "--"}</td>
      <td>${lr.normalRange || "--"}</td>
      <td><span class="status-pill ${statusPillClass(lr.status)}">${humanizeStatus(lr.status)}</span></td>
    </tr>
  `).join("");
}

/* ===================== TODAY'S QUEUE ===================== */

async function loadTodayQueue(doctorId) {
  const res = await apiGet(`/api/appointments/queue/${doctorId}`);
  const body = document.querySelector("#queue-table tbody");
  const badge = document.getElementById("queue-count-badge");

  if (!res.data.success || !res.data.data || res.data.data.length === 0) {
    body.innerHTML = `<tr><td colspan="5" class="empty-row">No patients in queue today.</td></tr>`;
    badge.textContent = "0 patients today";
    return;
  }

  const queue = res.data.data;
  badge.textContent = `${queue.length} patient${queue.length === 1 ? "" : "s"} today`;

  body.innerHTML = queue.map(a => `
    <tr>
      <td>#${String(a.tokenNumber).padStart(3, "0")}</td>
      <td>Patient #${a.patientId}</td>
      <td>${a.timeSlot || "--"}</td>
      <td><span class="status-pill ${statusPillClass(a.status)}">${humanizeStatus(a.status)}</span></td>
      <td><button class="btn-small primary" onclick="quickSearchById(${a.patientId})">View</button></td>
    </tr>
  `).join("");
}

function quickSearchById(patientId) {
  // Queue gives internal DB id, not the unique MDID string, so we just
  // let the doctor know to search manually for now (kept simple/honest).
  showSection("search");
  showMessage("search-msg", "Please search using the patient's Unique Health ID shown on their card.", "info");
}

/* ===================== MODAL: ADD PRESCRIPTION ===================== */

function openPrescriptionModal() {
  clearMessage("rx-modal-msg");
  document.getElementById("rx-notes").value = "";
  document.getElementById("rx-medicine-list").innerHTML = "";
  medicineRowCount = 0;
  addMedicineRow();
  document.getElementById("rx-modal").style.display = "flex";
}

function addMedicineRow() {
  medicineRowCount++;
  const rowId = "med-row-" + medicineRowCount;
  const list = document.getElementById("rx-medicine-list");

  const row = document.createElement("div");
  row.className = "medicine-row";
  row.id = rowId;
  row.innerHTML = `
    <input type="number" placeholder="Medicine ID" class="med-id" />
    <input type="text" placeholder="Dosage e.g. 500mg" class="med-dosage" />
    <input type="text" placeholder="Frequency e.g. Twice daily" class="med-freq" />
    <button class="remove-medicine-btn" onclick="document.getElementById('${rowId}').remove()">&times;</button>
  `;
  list.appendChild(row);
}

async function submitPrescription() {
  clearMessage("rx-modal-msg");

  if (!currentReportPatient) {
    showMessage("rx-modal-msg", "No patient selected.", "error");
    return;
  }

  const session = getSession();
  const notes = document.getElementById("rx-notes").value.trim();

  const medicines = [];
  document.querySelectorAll(".medicine-row").forEach(row => {
    const medicineId = row.querySelector(".med-id").value;
    const dosage = row.querySelector(".med-dosage").value.trim();
    const frequency = row.querySelector(".med-freq").value.trim();
    if (medicineId) {
      medicines.push({
        medicineId: parseInt(medicineId, 10),
        dosage: dosage,
        frequency: frequency,
        durationDays: 5,
        instructions: ""
      });
    }
  });

  if (medicines.length === 0) {
    showMessage("rx-modal-msg", "Please add at least one medicine with its ID.", "error");
    return;
  }

  showLoading();
  const res = await apiPost("/api/prescriptions/add", {
    patientUniqueId: currentReportPatient.uniqueId,
    doctorId: session.uniqueId,
    notes: notes,
    medicines: medicines
  });
  hideLoading();

  if (res.data.success) {
    showMessage("rx-modal-msg", res.data.message, "success");
    setTimeout(async () => {
      closeModal("rx-modal");
      await searchPatient(); // refresh report to show new prescription
    }, 1000);
  } else {
    showMessage("rx-modal-msg", res.data.message || "Could not save prescription.", "error");
  }
}

/* ===================== MODAL: ORDER LAB TEST ===================== */

function openLabOrderModal() {
  clearMessage("lab-modal-msg");
  document.getElementById("lab-test-name").value = "";
  document.getElementById("lab-test-date").value = new Date().toISOString().split("T")[0];
  document.getElementById("lab-normal-range").value = "";
  document.getElementById("lab-modal").style.display = "flex";
}

async function submitLabOrder() {
  clearMessage("lab-modal-msg");

  if (!currentReportPatient) {
    showMessage("lab-modal-msg", "No patient selected.", "error");
    return;
  }

  const testName = document.getElementById("lab-test-name").value.trim();
  const testDate = document.getElementById("lab-test-date").value;
  const normalRange = document.getElementById("lab-normal-range").value.trim();

  if (!testName || !testDate) {
    showMessage("lab-modal-msg", "Please enter the test name and date.", "error");
    return;
  }

  showLoading();
  const res = await apiPost("/api/lab-reports/add", {
    patientId: currentReportPatient.id,
    testName: testName,
    testDate: testDate,
    normalRange: normalRange,
    result: "Pending"
  });
  hideLoading();

  if (res.data.success) {
    showMessage("lab-modal-msg", res.data.message, "success");
    setTimeout(() => closeModal("lab-modal"), 1000);
  } else {
    showMessage("lab-modal-msg", res.data.message || "Could not order lab test.", "error");
  }
}

/* ===================== MODAL: ADMIT TO BED ===================== */

async function openBedModal() {
  clearMessage("bed-modal-msg");
  const select = document.getElementById("bed-select");
  select.innerHTML = `<option value="">Loading beds...</option>`;
  document.getElementById("bed-modal").style.display = "flex";

  const res = await apiGet("/api/beds/available");
  if (!res.data.success || !res.data.data || res.data.data.length === 0) {
    select.innerHTML = `<option value="">No beds available</option>`;
    return;
  }

  select.innerHTML = res.data.data
    .map(b => `<option value="${b.bedNumber}">${b.bedNumber} - ${b.ward}</option>`)
    .join("");
}

async function submitBedAdmission() {
  clearMessage("bed-modal-msg");

  if (!currentReportPatient) {
    showMessage("bed-modal-msg", "No patient selected.", "error");
    return;
  }

  const bedNumber = document.getElementById("bed-select").value;
  if (!bedNumber) {
    showMessage("bed-modal-msg", "Please select a bed.", "error");
    return;
  }

  showLoading();
  const res = await apiPost(
    `/api/beds/admit?bedNumber=${encodeURIComponent(bedNumber)}&patientUniqueId=${encodeURIComponent(currentReportPatient.uniqueId)}`,
    null
  );
  hideLoading();

  if (res.data.success) {
    showMessage("bed-modal-msg", res.data.message, "success");
    setTimeout(() => closeModal("bed-modal"), 1000);
  } else {
    showMessage("bed-modal-msg", res.data.message || "Could not admit patient.", "error");
  }
}

/* ===================== MODAL: REFER PATIENT ===================== */

function openReferralModal() {
  clearMessage("ref-modal-msg");
  document.getElementById("ref-hospital").value = "";
  document.getElementById("ref-reason").value = "";
  document.getElementById("referral-modal").style.display = "flex";
}

async function submitReferral() {
  clearMessage("ref-modal-msg");

  if (!currentReportPatient) {
    showMessage("ref-modal-msg", "No patient selected.", "error");
    return;
  }

  const session = getSession();
  const hospital = document.getElementById("ref-hospital").value.trim();
  const reason = document.getElementById("ref-reason").value.trim();
  const urgency = document.getElementById("ref-urgency").value;

  if (!hospital || !reason) {
    showMessage("ref-modal-msg", "Please fill in the hospital and reason.", "error");
    return;
  }

  showLoading();
  const res = await apiPost(
    `/api/referrals/create?patientUniqueId=${encodeURIComponent(currentReportPatient.uniqueId)}&doctorId=${encodeURIComponent(session.uniqueId)}&toHospital=${encodeURIComponent(hospital)}&reason=${encodeURIComponent(reason)}&urgency=${urgency}`,
    null
  );
  hideLoading();

  if (res.data.success) {
    showMessage("ref-modal-msg", res.data.message, "success");
    setTimeout(() => closeModal("referral-modal"), 1000);
  } else {
    showMessage("ref-modal-msg", res.data.message || "Could not create referral.", "error");
  }
}

/* ===================== SHARED MODAL CLOSE ===================== */

function closeModal(id) {
  document.getElementById(id).style.display = "none";
}