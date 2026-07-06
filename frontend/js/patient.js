/* ==========================================================
   MediDoc - patient.js
   All logic for patient-dashboard.html
   ========================================================== */

let currentPatient = null; // full patient object once loaded

/* ===================== INIT ===================== */

document.addEventListener("DOMContentLoaded", async () => {
  const session = requireAuth("PATIENT");
  if (!session) return;

  renderTodayDate();
  document.getElementById("patient-avatar").textContent = getInitials(session.fullName);

  await loadPatientOverview(session.uniqueId);
  await loadDoctorsForBooking();
  await loadAppointments(session.uniqueId);
  await loadPrescriptions(session.uniqueId);
  await loadLabReports(session.uniqueId);
  await loadFamilyMembers(session.uniqueId);
});

/* ===================== SECTION SWITCHING ===================== */

function showSection(name) {
  document.querySelectorAll(".section").forEach(s => s.classList.remove("active"));
  document.querySelectorAll(".smenu-item").forEach(m => m.classList.remove("active"));

  document.getElementById("section-" + name).classList.add("active");
  document.querySelector(`.smenu-item[data-section="${name}"]`).classList.add("active");

  const titles = {
    overview: "My Health Dashboard",
    appointments: "Appointments",
    prescriptions: "My Prescriptions",
    "lab-reports": "Lab Reports",
    family: "Family Members"
  };
  document.getElementById("page-title").textContent = titles[name] || "MediDoc";
}

/* ===================== OVERVIEW / PATIENT INFO ===================== */

async function loadPatientOverview(uniqueId) {
  const res = await apiGet(`/api/patients/${uniqueId}`);

  if (!res.data.success) {
    document.getElementById("hero-name").textContent = "Could not load profile";
    return;
  }

  const p = res.data.data;
  currentPatient = p;

  document.getElementById("hero-greeting").textContent = greetingForNow() + ",";
  document.getElementById("hero-name").textContent = p.fullName;
  document.getElementById("hero-id").textContent =
    `${p.uniqueId} | Blood: ${formatBloodGroup(p.bloodGroup)} | Age: ${p.age}`;

  const grid = document.getElementById("patient-info-grid");
  grid.innerHTML = `
    <div class="pfield"><div class="pfield-label">Full Name</div><div class="pfield-value">${p.fullName}</div></div>
    <div class="pfield"><div class="pfield-label">Age / Gender</div><div class="pfield-value">${p.age} / ${humanizeStatus(p.gender)}</div></div>
    <div class="pfield"><div class="pfield-label">Blood Group</div><div class="pfield-value"><span class="tag tag-blood">${formatBloodGroup(p.bloodGroup)}</span></div></div>
    <div class="pfield"><div class="pfield-label">Mobile</div><div class="pfield-value">${p.mobileNumber || "--"}</div></div>
    <div class="pfield"><div class="pfield-label">Address</div><div class="pfield-value">${p.address || "--"}</div></div>
    <div class="pfield"><div class="pfield-label">Insurance</div><div class="pfield-value">${p.isPmjayEligible ? '<span class="status-pill pill-green">PM-JAY Eligible</span>' : '<span class="status-pill pill-gray">Not linked</span>'}</div></div>
  `;
}

function greetingForNow() {
  const hour = new Date().getHours();
  if (hour < 12) return "Good morning";
  if (hour < 17) return "Good afternoon";
  return "Good evening";
}

/* ===================== APPOINTMENTS ===================== */

async function loadDoctorsForBooking() {
  const res = await apiGet("/api/doctors/active");
  const select = document.getElementById("appt-doctor-select");

  if (!res.data.success || !res.data.data || res.data.data.length === 0) {
    select.innerHTML = `<option value="">No doctors available</option>`;
    return;
  }

  select.innerHTML = res.data.data
    .map(doc => `<option value="${doc.doctorId}">${doc.name} - ${doc.specialization}</option>`)
    .join("");
}

async function loadAppointments(uniqueId) {
  const res = await apiGet(`/api/appointments/patient/${uniqueId}`);

  const upcomingBody = document.querySelector("#upcoming-appointments-table tbody");
  const allBody = document.querySelector("#all-appointments-table tbody");

  if (!res.data.success || !res.data.data || res.data.data.length === 0) {
    const emptyRow = `<tr><td colspan="5" class="empty-row">No appointments yet. Book your first appointment above.</td></tr>`;
    upcomingBody.innerHTML = emptyRow;
    allBody.innerHTML = emptyRow;
    return;
  }

  const appts = res.data.data;

  allBody.innerHTML = appts.map(renderAppointmentRow).join("");

  const upcoming = appts.filter(a => a.status === "BOOKED" || a.status === "IN_PROGRESS");
  upcomingBody.innerHTML = upcoming.length
    ? upcoming.map(renderAppointmentRow).join("")
    : `<tr><td colspan="5" class="empty-row">No upcoming appointments.</td></tr>`;
}

function renderAppointmentRow(a) {
  return `
    <tr>
      <td>#${String(a.tokenNumber).padStart(3, "0")}</td>
      <td>Doctor ID ${a.doctorId}</td>
      <td>${formatDate(a.appointmentDate)}</td>
      <td>${a.timeSlot || "--"}</td>
      <td><span class="status-pill ${statusPillClass(a.status)}">${humanizeStatus(a.status)}</span></td>
    </tr>
  `;
}

async function bookAppointment() {
  clearMessage("appt-msg");

  const session = getSession();
  const doctorId = document.getElementById("appt-doctor-select").value;
  const date = document.getElementById("appt-date").value;
  const timeSlot = document.getElementById("appt-time").value.trim();
  const notes = document.getElementById("appt-notes").value.trim();

  if (!doctorId || !date) {
    showMessage("appt-msg", "Please select a doctor and a date.", "error");
    return;
  }

  showLoading();
  const res = await apiPost("/api/appointments/book", {
    patientUniqueId: session.uniqueId,
    doctorId: doctorId,
    appointmentDate: date,
    timeSlot: timeSlot,
    notes: notes,
    bookedBy: "PATIENT"
  });
  hideLoading();

  if (res.data.success) {
    showMessage("appt-msg", res.data.message, "success");
    document.getElementById("appt-time").value = "";
    document.getElementById("appt-notes").value = "";
    await loadAppointments(session.uniqueId);
  } else {
    showMessage("appt-msg", res.data.message || "Could not book appointment.", "error");
  }
}

/* ===================== PRESCRIPTIONS ===================== */

async function loadPrescriptions(uniqueId) {
  const res = await apiGet(`/api/prescriptions/patient/${uniqueId}`);
  const body = document.querySelector("#prescriptions-table tbody");

  if (!res.data.success || !res.data.data || res.data.data.length === 0) {
    body.innerHTML = `<tr><td colspan="3" class="empty-row">No prescriptions yet.</td></tr>`;
    return;
  }

  body.innerHTML = res.data.data.map(rx => `
    <tr>
      <td>${formatDate(rx.prescribedDate)}</td>
      <td>${rx.notes || "--"}</td>
      <td><span class="status-pill ${statusPillClass(rx.status)}">${humanizeStatus(rx.status)}</span></td>
    </tr>
  `).join("");
}

/* ===================== LAB REPORTS ===================== */

async function loadLabReports(uniqueId) {
  const res = await apiGet(`/api/lab-reports/patient/${uniqueId}`);
  const body = document.querySelector("#lab-reports-table tbody");

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

/* ===================== FAMILY MEMBERS ===================== */

async function loadFamilyMembers(uniqueId) {
  const res = await apiGet(`/api/family/${uniqueId}`);
  const body = document.querySelector("#family-table tbody");

  if (!res.data.success || !res.data.data || res.data.data.length === 0) {
    body.innerHTML = `<tr><td colspan="3" class="empty-row">No family members linked yet.</td></tr>`;
    return;
  }

  body.innerHTML = res.data.data.map(f => `
    <tr>
      <td>Patient #${f.memberPatientId}</td>
      <td>${f.relationship || "--"}</td>
      <td>${formatDate(f.createdAt)}</td>
    </tr>
  `).join("");
}

async function linkFamilyMember() {
  clearMessage("family-msg");

  const session = getSession();
  const memberId = document.getElementById("family-member-id").value.trim();
  const relationship = document.getElementById("family-relationship").value;

  if (!memberId) {
    showMessage("family-msg", "Please enter the family member's Unique ID.", "error");
    return;
  }

  showLoading();
  const res = await apiPost(
    `/api/family/link?primaryUniqueId=${encodeURIComponent(session.uniqueId)}&memberUniqueId=${encodeURIComponent(memberId)}&relationship=${encodeURIComponent(relationship)}`,
    null
  );
  hideLoading();

  if (res.data.success) {
    showMessage("family-msg", res.data.message, "success");
    document.getElementById("family-member-id").value = "";
    await loadFamilyMembers(session.uniqueId);
  } else {
    showMessage("family-msg", res.data.message || "Could not link family member.", "error");
  }
}

/* ===================== BLOOD REQUEST MODAL ===================== */

function openBloodRequestModal() {
  clearMessage("blood-modal-msg");
  document.getElementById("blood-modal").style.display = "flex";
}

function closeModal(id) {
  document.getElementById(id).style.display = "none";
}

async function submitBloodRequest() {
  clearMessage("blood-modal-msg");

  const session = getSession();
  const bloodGroup = document.getElementById("blood-req-group").value;
  const units = document.getElementById("blood-req-units").value;
  const priority = document.getElementById("blood-req-priority").value;
  const purpose = document.getElementById("blood-req-purpose").value.trim();

  showLoading();
  const res = await apiPost(
    `/api/blood/request?bloodGroup=${bloodGroup}&units=${units}&priority=${priority}&purpose=${encodeURIComponent(purpose)}`,
    null
  );
  hideLoading();

  if (res.data.success) {
    showMessage("blood-modal-msg", res.data.message, "success");
    setTimeout(() => closeModal("blood-modal"), 1200);
  } else {
    showMessage("blood-modal-msg", res.data.message || "Could not submit blood request.", "error");
  }
}

/* ===================== AMBULANCE MODAL ===================== */

function openAmbulanceModal() {
  clearMessage("amb-modal-msg");
  document.getElementById("ambulance-modal").style.display = "flex";
}

async function submitAmbulanceRequest() {
  clearMessage("amb-modal-msg");

  const session = getSession();
  const pickup = document.getElementById("amb-pickup").value.trim();
  const phone = document.getElementById("amb-phone").value.trim();
  const priority = document.getElementById("amb-priority").value;

  if (!pickup || !phone) {
    showMessage("amb-modal-msg", "Please fill in pickup address and phone number.", "error");
    return;
  }

  showLoading();
  const res = await apiPost("/api/ambulances/request", {
    requesterName: session.fullName,
    requesterPhone: phone,
    pickupAddress: pickup,
    priority: priority
  });
  hideLoading();

  if (res.data.success) {
    showMessage("amb-modal-msg", res.data.message, "success");
    setTimeout(() => closeModal("ambulance-modal"), 1200);
  } else {
    showMessage("amb-modal-msg", res.data.message || "Could not request ambulance.", "error");
  }
}