/* ==========================================================
   MediDoc - admin.js
   All logic for admin-dashboard.html
   ========================================================== */

document.addEventListener("DOMContentLoaded", async () => {
  const session = requireAuth("ADMIN");
  if (!session) return;

  renderTodayDate();
  document.getElementById("admin-avatar").textContent = getInitials(session.fullName);

  await loadOverviewStats();
  await loadOverviewDoctors();
  await loadOverviewAlerts();
  await loadAllDoctors();
  await loadAllAlerts();
  await loadAuditLogs();
});

/* ===================== SECTION SWITCHING ===================== */

function showSection(name) {
  document.querySelectorAll(".section").forEach(s => s.classList.remove("active"));
  document.querySelectorAll(".smenu-item").forEach(m => m.classList.remove("active"));

  document.getElementById("section-" + name).classList.add("active");
  document.querySelector(`.smenu-item[data-section="${name}"]`).classList.add("active");

  const titles = {
    overview: "Admin Overview",
    doctors: "Doctor Management",
    alerts: "Alerts",
    audit: "Audit Logs"
  };
  document.getElementById("page-title").textContent = titles[name] || "MediDoc";
}

/* ===================== OVERVIEW STATS ===================== */

async function loadOverviewStats() {
  const res = await apiGet("/api/analytics/summary");
  if (!res.data.success) return;

  const d = res.data.data;
  document.getElementById("stat-total-patients").textContent = d.totalPatients ?? "--";
  document.getElementById("stat-active-doctors").textContent = d.activeDoctors ?? "--";
  document.getElementById("stat-beds-available").textContent = d.availableBeds ?? "--";
  document.getElementById("stat-blood-units").textContent = d.totalBloodUnits ?? "--";
}

/* ===================== OVERVIEW: DOCTORS PREVIEW ===================== */

async function loadOverviewDoctors() {
  const res = await apiGet("/api/doctors");
  const body = document.querySelector("#overview-doctors-table tbody");

  if (!res.data.success || !res.data.data || res.data.data.length === 0) {
    body.innerHTML = `<tr><td colspan="3" class="empty-row">No doctors found.</td></tr>`;
    return;
  }

  const top5 = res.data.data.slice(0, 5);
  body.innerHTML = top5.map(doc => `
    <tr>
      <td>${doc.name}</td>
      <td>${doc.department}</td>
      <td><span class="status-pill ${doc.status === "ACTIVE" ? "pill-green" : "pill-amber"}">${humanizeStatus(doc.status)}</span></td>
    </tr>
  `).join("");
}

/* ===================== OVERVIEW: ALERTS PREVIEW ===================== */

async function loadOverviewAlerts() {
  const res = await apiGet("/api/alerts/active");
  const list = document.getElementById("overview-alerts-list");

  if (!res.data.success || !res.data.data || res.data.data.length === 0) {
    list.innerHTML = `<div class="empty-row">No active alerts.</div>`;
    return;
  }

  list.innerHTML = res.data.data.slice(0, 4).map(renderAlertItem).join("");
}

function renderAlertItem(a) {
  const sev = (a.severity || "MEDIUM").toLowerCase();
  return `
    <div class="alert-item severity-${sev}">
      <div class="alert-item-title">${a.title}</div>
      <div class="alert-item-msg">${a.message}</div>
      <div class="alert-item-meta">${humanizeStatus(a.alertType)} &middot; ${formatDateTime(a.createdAt)}</div>
    </div>
  `;
}

/* ===================== DOCTOR MANAGEMENT ===================== */

async function loadAllDoctors() {
  const res = await apiGet("/api/doctors");
  const body = document.querySelector("#all-doctors-table tbody");

  if (!res.data.success || !res.data.data || res.data.data.length === 0) {
    body.innerHTML = `<tr><td colspan="6" class="empty-row">No doctors found.</td></tr>`;
    return;
  }

  body.innerHTML = res.data.data.map(doc => `
    <tr>
      <td>${doc.doctorId}</td>
      <td>${doc.name}</td>
      <td>${doc.department}</td>
      <td>${doc.experienceYears ?? "--"} yrs</td>
      <td><span class="status-pill ${doc.status === "ACTIVE" ? "pill-green" : "pill-amber"}">${humanizeStatus(doc.status)}</span></td>
      <td><button class="btn-small" onclick="deleteDoctor('${doc.doctorId}')">Remove</button></td>
    </tr>
  `).join("");
}

async function addDoctor() {
  clearMessage("doc-add-msg");

  const payload = {
    doctorId: document.getElementById("doc-id").value.trim(),
    name: document.getElementById("doc-name").value.trim(),
    specialization: document.getElementById("doc-specialization").value.trim(),
    department: document.getElementById("doc-department").value.trim(),
    mobile: document.getElementById("doc-mobile").value.trim(),
    experienceYears: parseInt(document.getElementById("doc-experience").value || "0", 10),
    qualification: document.getElementById("doc-qualification").value.trim(),
    employeePassword: document.getElementById("doc-password").value.trim()
  };

  if (!payload.doctorId || !payload.name || !payload.department || !payload.employeePassword) {
    showMessage("doc-add-msg", "Please fill in Doctor ID, Name, Department, and Password.", "error");
    return;
  }

  showLoading();
  const res = await apiPost("/api/doctors", payload);
  hideLoading();

  if (res.data.success) {
    showMessage("doc-add-msg", res.data.message, "success");
    ["doc-id","doc-name","doc-specialization","doc-department","doc-mobile","doc-experience","doc-qualification","doc-password"]
      .forEach(id => document.getElementById(id).value = "");
    await loadAllDoctors();
    await loadOverviewDoctors();
  } else {
    showMessage("doc-add-msg", res.data.message || "Could not add doctor.", "error");
  }
}

async function deleteDoctor(doctorId) {
  if (!confirm(`Remove doctor ${doctorId}? This cannot be undone.`)) return;

  showLoading();
  const res = await apiDelete(`/api/doctors/${doctorId}`);
  hideLoading();

  if (res.data.success) {
    await loadAllDoctors();
    await loadOverviewDoctors();
  } else {
    alert(res.data.message || "Could not remove doctor.");
  }
}

/* ===================== ALERTS ===================== */

async function broadcastAlert() {
  clearMessage("alert-msg");

  const title = document.getElementById("alert-title").value.trim();
  const message = document.getElementById("alert-message").value.trim();
  const alertType = document.getElementById("alert-type").value;
  const severity = document.getElementById("alert-severity").value;

  if (!title || !message) {
    showMessage("alert-msg", "Please fill in the title and message.", "error");
    return;
  }

  showLoading();
  const res = await apiPost("/api/alerts", {
    title: title,
    message: message,
    alertType: alertType,
    severity: severity
  });
  hideLoading();

  if (res.data.success) {
    showMessage("alert-msg", res.data.message, "success");
    document.getElementById("alert-title").value = "";
    document.getElementById("alert-message").value = "";
    await loadAllAlerts();
    await loadOverviewAlerts();
  } else {
    showMessage("alert-msg", res.data.message || "Could not broadcast alert.", "error");
  }
}

async function loadAllAlerts() {
  const res = await apiGet("/api/alerts");
  const list = document.getElementById("all-alerts-list");

  if (!res.data.success || !res.data.data || res.data.data.length === 0) {
    list.innerHTML = `<div class="empty-row">No alerts have been broadcast yet.</div>`;
    return;
  }

  list.innerHTML = res.data.data.map(renderAlertItem).join("");
}

/* ===================== AUDIT LOGS ===================== */

async function loadAuditLogs() {
  const res = await apiGet("/api/audit-logs/recent");
  const body = document.querySelector("#audit-table tbody");

  if (!res.data.success || !res.data.data || res.data.data.length === 0) {
    body.innerHTML = `<tr><td colspan="5" class="empty-row">No audit log entries yet.</td></tr>`;
    return;
  }

  body.innerHTML = res.data.data.map(log => `
    <tr>
      <td>${formatDateTime(log.timestamp)}</td>
      <td>${log.userId || "--"}</td>
      <td>${log.role || "--"}</td>
      <td>${log.action}</td>
      <td>${log.patientUniqueNumber || "--"}</td>
    </tr>
  `).join("");
}