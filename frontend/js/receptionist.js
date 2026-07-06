/* ==========================================================
   MediDoc - receptionist.js
   All logic for receptionist-dashboard.html
   ========================================================== */

document.addEventListener("DOMContentLoaded", async () => {
  const session = requireAuth("RECEPTIONIST");
  if (!session) return;

  renderTodayDate();
  document.getElementById("rec-avatar").textContent = getInitials(session.fullName);

  await loadDoctorsForBooking();
});

/* ===================== SECTION SWITCHING ===================== */

function showSection(name) {
  document.querySelectorAll(".section").forEach(s => s.classList.remove("active"));
  document.querySelectorAll(".smenu-item").forEach(m => m.classList.remove("active"));

  document.getElementById("section-" + name).classList.add("active");
  document.querySelector(`.smenu-item[data-section="${name}"]`).classList.add("active");

  const titles = {
    register: "Register New Patient",
    book: "Book Appointment",
    today: "Today's Appointments"
  };
  document.getElementById("page-title").textContent = titles[name] || "MediDoc";
}

/* ===================== PATIENT REGISTRATION ===================== */

function isValidMobile(mobile) {
  return /^[6-9]\d{9}$/.test(mobile);
}

async function registerPatient() {
  clearMessage("register-msg");
  document.getElementById("generated-id-card").style.display = "none";

  const fullName = document.getElementById("r-fullname").value.trim();
  const age = document.getElementById("r-age").value;
  const gender = document.getElementById("r-gender").value;
  const bloodGroup = document.getElementById("r-blood-group").value;
  const mobile = document.getElementById("r-mobile").value.trim();
  const address = document.getElementById("r-address").value.trim();
  const emergencyName = document.getElementById("r-emergency-name").value.trim();
  const emergencyPhone = document.getElementById("r-emergency-phone").value.trim();

  if (!fullName || !age || !gender) {
    showMessage("register-msg", "Please fill in Full Name, Age, and Gender.", "error");
    return;
  }
  if (!isValidMobile(mobile)) {
    showMessage("register-msg", "Please enter a valid 10-digit mobile number.", "error");
    return;
  }

  const payload = {
    fullName: fullName,
    age: parseInt(age, 10),
    gender: gender,
    mobileNumber: mobile,
    address: address,
    emergencyContactName: emergencyName,
    emergencyContactPhone: emergencyPhone,
    isPmjayEligible: false
  };
  if (bloodGroup) payload.bloodGroup = bloodGroup;

  showLoading();
  const res = await apiPost("/api/patients/register", payload);
  hideLoading();

  if (res.data.success) {
    showMessage("register-msg", res.data.message, "success");
    const uniqueId = res.data.data ? res.data.data.uniqueId : "--";
    document.getElementById("generated-id-display").textContent = uniqueId;
    document.getElementById("generated-id-card").style.display = "block";

    ["r-fullname","r-age","r-gender","r-blood-group","r-mobile","r-address","r-emergency-name","r-emergency-phone"]
      .forEach(id => document.getElementById(id).value = "");
  } else {
    showMessage("register-msg", res.data.message || "Registration failed. Please try again.", "error");
  }
}

/* ===================== APPOINTMENT BOOKING ===================== */

async function loadDoctorsForBooking() {
  const res = await apiGet("/api/doctors/active");

  const bookSelect = document.getElementById("appt-doctor-select");
  const todaySelect = document.getElementById("today-doctor-select");

  if (!res.data.success || !res.data.data || res.data.data.length === 0) {
    bookSelect.innerHTML = `<option value="">No doctors available</option>`;
    todaySelect.innerHTML = `<option value="">No doctors available</option>`;
    return;
  }

  const options = res.data.data
    .map(doc => `<option value="${doc.doctorId}">${doc.name} - ${doc.specialization}</option>`)
    .join("");

  bookSelect.innerHTML = options;
  todaySelect.innerHTML = `<option value="">Select a doctor to view queue</option>` + options;
}

async function bookAppointment() {
  clearMessage("appt-msg");

  const patientId = document.getElementById("appt-patient-id").value.trim();
  const doctorId = document.getElementById("appt-doctor-select").value;
  const date = document.getElementById("appt-date").value;
  const timeSlot = document.getElementById("appt-time").value.trim();
  const notes = document.getElementById("appt-notes").value.trim();

  if (!patientId || !doctorId || !date) {
    showMessage("appt-msg", "Please fill in Patient ID, Doctor, and Date.", "error");
    return;
  }

  showLoading();
  const res = await apiPost("/api/appointments/book", {
    patientUniqueId: patientId,
    doctorId: doctorId,
    appointmentDate: date,
    timeSlot: timeSlot,
    notes: notes,
    bookedBy: "RECEPTIONIST"
  });
  hideLoading();

  if (res.data.success) {
    showMessage("appt-msg", res.data.message, "success");
    document.getElementById("appt-patient-id").value = "";
    document.getElementById("appt-time").value = "";
    document.getElementById("appt-notes").value = "";
  } else {
    showMessage("appt-msg", res.data.message || "Could not book appointment.", "error");
  }
}

/* ===================== TODAY'S APPOINTMENTS ===================== */

async function loadTodayAppointments() {
  const doctorId = document.getElementById("today-doctor-select").value;
  const body = document.querySelector("#today-table tbody");

  if (!doctorId) {
    body.innerHTML = `<tr><td colspan="4" class="empty-row">Select a doctor above.</td></tr>`;
    return;
  }

  body.innerHTML = `<tr><td colspan="4" class="empty-row">Loading...</td></tr>`;

  const res = await apiGet(`/api/appointments/queue/${doctorId}`);

  if (!res.data.success || !res.data.data || res.data.data.length === 0) {
    body.innerHTML = `<tr><td colspan="4" class="empty-row">No appointments today for this doctor.</td></tr>`;
    return;
  }

  body.innerHTML = res.data.data.map(a => `
    <tr>
      <td>#${String(a.tokenNumber).padStart(3, "0")}</td>
      <td>Patient #${a.patientId}</td>
      <td>${a.timeSlot || "--"}</td>
      <td><span class="status-pill ${statusPillClass(a.status)}">${humanizeStatus(a.status)}</span></td>
    </tr>
  `).join("");
}