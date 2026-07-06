/* ==========================================================
   MediDoc - beds.js
   All logic for beds.html
   ========================================================== */

document.addEventListener("DOMContentLoaded", async () => {
  const session = requireAuth(); // any staff role can view beds
  if (!session) return;

  renderTodayDate();
  await loadBedOverview();
  await populateBedSelects();
});

/* ===================== SECTION SWITCHING ===================== */

function showSection(name) {
  document.querySelectorAll(".section").forEach(s => s.classList.remove("active"));
  document.querySelectorAll(".smenu-item").forEach(m => m.classList.remove("active"));

  document.getElementById("section-" + name).classList.add("active");
  document.querySelector(`.smenu-item[data-section="${name}"]`).classList.add("active");

  const titles = {
    overview: "Real-time Bed Availability",
    admit: "Admit Patient",
    discharge: "Discharge Patient"
  };
  document.getElementById("page-title").textContent = titles[name] || "MediDoc";
}

/* ===================== BED OVERVIEW (grouped by ward) ===================== */

async function loadBedOverview() {
  const res = await apiGet("/api/beds");
  const container = document.getElementById("ward-groups");

  if (!res.data.success || !res.data.data || res.data.data.length === 0) {
    container.innerHTML = `<div class="empty-row">No beds found.</div>`;
    return;
  }

  const beds = res.data.data;

  const total = beds.length;
  const available = beds.filter(b => b.status === "AVAILABLE").length;
  const occupied = beds.filter(b => b.status === "OCCUPIED").length;
  const maintenance = beds.filter(b => b.status === "MAINTENANCE" || b.status === "CLEANING").length;

  document.getElementById("stat-total-beds").textContent = total;
  document.getElementById("stat-available-beds").textContent = available;
  document.getElementById("stat-occupied-beds").textContent = occupied;
  document.getElementById("stat-maintenance-beds").textContent = maintenance;

  // Group beds by ward
  const wards = {};
  beds.forEach(b => {
    if (!wards[b.ward]) wards[b.ward] = [];
    wards[b.ward].push(b);
  });

  container.innerHTML = Object.keys(wards).map(wardName => `
    <div class="ward-block">
      <div class="ward-title">${wardName}</div>
      <div class="bed-grid">
        ${wards[wardName].map(renderBedCell).join("")}
      </div>
    </div>
  `).join("");
}

function renderBedCell(bed) {
  const statusClass = bed.status === "AVAILABLE" ? "available"
    : bed.status === "OCCUPIED" ? "occupied"
    : "maintenance";

  const statusText = bed.status === "AVAILABLE" ? "Available"
    : bed.status === "OCCUPIED" ? "Occupied"
    : humanizeStatus(bed.status);

  return `
    <div class="bed-cell ${statusClass}">
      <div class="bed-num">${bed.bedNumber}</div>
      <div class="bed-status-text">${statusText}</div>
    </div>
  `;
}

/* ===================== ADMIT / DISCHARGE SELECTS ===================== */

async function populateBedSelects() {
  const res = await apiGet("/api/beds");
  if (!res.data.success || !res.data.data) return;

  const beds = res.data.data;
  const availableBeds = beds.filter(b => b.status === "AVAILABLE");
  const occupiedBeds = beds.filter(b => b.status === "OCCUPIED");

  const admitSelect = document.getElementById("admit-bed-select");
  admitSelect.innerHTML = availableBeds.length
    ? availableBeds.map(b => `<option value="${b.bedNumber}">${b.bedNumber} - ${b.ward}</option>`).join("")
    : `<option value="">No available beds</option>`;

  const dischargeSelect = document.getElementById("discharge-bed-select");
  dischargeSelect.innerHTML = occupiedBeds.length
    ? occupiedBeds.map(b => `<option value="${b.bedNumber}">${b.bedNumber} - ${b.ward}</option>`).join("")
    : `<option value="">No occupied beds</option>`;
}

/* ===================== ADMIT PATIENT ===================== */

async function admitPatient() {
  clearMessage("admit-msg");

  const patientId = document.getElementById("admit-patient-id").value.trim();
  const bedNumber = document.getElementById("admit-bed-select").value;

  if (!patientId || !bedNumber) {
    showMessage("admit-msg", "Please enter a Patient ID and select a bed.", "error");
    return;
  }

  showLoading();
  const res = await apiPost(
    `/api/beds/admit?bedNumber=${encodeURIComponent(bedNumber)}&patientUniqueId=${encodeURIComponent(patientId)}`,
    null
  );
  hideLoading();

  if (res.data.success) {
    showMessage("admit-msg", res.data.message, "success");
    document.getElementById("admit-patient-id").value = "";
    await loadBedOverview();
    await populateBedSelects();
  } else {
    showMessage("admit-msg", res.data.message || "Could not admit patient.", "error");
  }
}

/* ===================== DISCHARGE PATIENT ===================== */

async function dischargePatient() {
  clearMessage("discharge-msg");

  const bedNumber = document.getElementById("discharge-bed-select").value;
  if (!bedNumber) {
    showMessage("discharge-msg", "Please select a bed to discharge.", "error");
    return;
  }

  showLoading();
  const res = await apiPut(`/api/beds/discharge/${encodeURIComponent(bedNumber)}`, null);
  hideLoading();

  if (res.data.success) {
    showMessage("discharge-msg", res.data.message, "success");
    await loadBedOverview();
    await populateBedSelects();
  } else {
    showMessage("discharge-msg", res.data.message || "Could not discharge patient.", "error");
  }
}