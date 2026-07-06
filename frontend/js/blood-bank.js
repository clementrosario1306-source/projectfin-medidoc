/* ==========================================================
   MediDoc - blood-bank.js
   All logic for blood-bank.html
   ========================================================== */

document.addEventListener("DOMContentLoaded", async () => {
  const session = requireAuth("BLOOD_BANK_OFFICER");
  if (!session) return;

  renderTodayDate();
  document.getElementById("officer-avatar").textContent = getInitials(session.fullName);

  await loadInventory();
  await loadPendingRequests();
  await loadEmergencyRequests();
  await loadDonors();
});

/* ===================== SECTION SWITCHING ===================== */

function showSection(name) {
  document.querySelectorAll(".section").forEach(s => s.classList.remove("active"));
  document.querySelectorAll(".smenu-item").forEach(m => m.classList.remove("active"));

  document.getElementById("section-" + name).classList.add("active");
  document.querySelector(`.smenu-item[data-section="${name}"]`).classList.add("active");

  const titles = {
    inventory: "Blood Bank Inventory",
    requests: "Blood Requests",
    donors: "Blood Donors",
    search: "Search Hospital"
  };
  document.getElementById("page-title").textContent = titles[name] || "MediDoc";
}

/* ===================== INVENTORY ===================== */

async function loadInventory() {
  const res = await apiGet("/api/blood/inventory");
  const grid = document.getElementById("blood-grid");

  if (!res.data.success || !res.data.data || res.data.data.length === 0) {
    grid.innerHTML = `<div class="empty-row">No blood inventory found.</div>`;
    return;
  }

  const inventory = res.data.data;
  const totalUnits = inventory.reduce((sum, b) => sum + (b.unitsAvailable || 0), 0);
  const lowStockCount = inventory.filter(b => (b.unitsAvailable || 0) < 10).length;

  document.getElementById("stat-total-units").textContent = totalUnits;
  document.getElementById("stat-low-stock").textContent = lowStockCount;

  const maxUnits = Math.max(...inventory.map(b => b.unitsAvailable || 0), 1);

  grid.innerHTML = inventory.map(b => {
    const units = b.unitsAvailable || 0;
    const isLow = units < 10;
    const pct = Math.round((units / maxUnits) * 100);
    return `
      <div class="blood-card ${isLow ? "low-stock" : ""}">
        <div class="blood-type">${formatBloodGroup(b.bloodGroup)}</div>
        <div class="blood-units">${units}</div>
        <div class="blood-label">${isLow ? "LOW STOCK!" : "units available"}</div>
        <div class="blood-bar"><div class="blood-fill" style="width:${pct}%"></div></div>
      </div>
    `;
  }).join("");

  await loadPendingRequestsCount();
}

async function loadPendingRequestsCount() {
  const res = await apiGet("/api/blood/requests/pending");
  document.getElementById("stat-pending-requests").textContent =
    res.data.success && res.data.data ? res.data.data.length : 0;
}

async function addStock() {
  clearMessage("add-stock-msg");

  const bloodGroup = document.getElementById("add-stock-group").value;
  const units = document.getElementById("add-stock-units").value;

  if (!units || units <= 0) {
    showMessage("add-stock-msg", "Please enter a valid number of units.", "error");
    return;
  }

  showLoading();
  const res = await apiPost(`/api/blood/add-stock?bloodGroup=${bloodGroup}&units=${units}`, null);
  hideLoading();

  if (res.data.success) {
    showMessage("add-stock-msg", res.data.message, "success");
    document.getElementById("add-stock-units").value = "";
    await loadInventory();
  } else {
    showMessage("add-stock-msg", res.data.message || "Could not add stock.", "error");
  }
}

async function issueBlood() {
  clearMessage("issue-msg");

  const bloodGroup = document.getElementById("issue-blood-group").value;
  const units = document.getElementById("issue-units").value;

  if (!units || units <= 0) {
    showMessage("issue-msg", "Please enter a valid number of units.", "error");
    return;
  }

  showLoading();
  const res = await apiPost(`/api/blood/issue?bloodGroup=${bloodGroup}&units=${units}`, null);
  hideLoading();

  if (res.data.success) {
    showMessage("issue-msg", res.data.message, "success");
    document.getElementById("issue-units").value = "";
    await loadInventory();
  } else {
    showMessage("issue-msg", res.data.message || "Could not issue blood.", "error");
  }
}

/* ===================== BLOOD REQUESTS ===================== */

async function loadPendingRequests() {
  const res = await apiGet("/api/blood/requests/pending");
  const body = document.querySelector("#pending-requests-table tbody");

  if (!res.data.success || !res.data.data || res.data.data.length === 0) {
    body.innerHTML = `<tr><td colspan="6" class="empty-row">No pending requests.</td></tr>`;
    return;
  }

  body.innerHTML = res.data.data.map(r => `
    <tr>
      <td>${r.patientId ? "Patient #" + r.patientId : "--"}</td>
      <td>${formatBloodGroup(r.bloodGroup)}</td>
      <td>${r.unitsRequired}</td>
      <td><span class="status-pill ${statusPillClass(r.priority)}">${humanizeStatus(r.priority)}</span></td>
      <td>${r.purpose || "--"}</td>
      <td><span class="status-pill ${statusPillClass(r.status)}">${humanizeStatus(r.status)}</span></td>
    </tr>
  `).join("");
}

async function loadEmergencyRequests() {
  const res = await apiGet("/api/blood/requests/emergency");
  const body = document.querySelector("#emergency-requests-table tbody");

  if (!res.data.success || !res.data.data || res.data.data.length === 0) {
    body.innerHTML = `<tr><td colspan="5" class="empty-row">No emergency requests.</td></tr>`;
    return;
  }

  body.innerHTML = res.data.data.map(r => `
    <tr>
      <td>${r.patientId ? "Patient #" + r.patientId : "--"}</td>
      <td>${formatBloodGroup(r.bloodGroup)}</td>
      <td>${r.unitsRequired}</td>
      <td>${r.purpose || "--"}</td>
      <td><span class="status-pill ${statusPillClass(r.status)}">${humanizeStatus(r.status)}</span></td>
    </tr>
  `).join("");
}

/* ===================== EMERGENCY REQUEST MODAL ===================== */

function openEmergencyModal() {
  clearMessage("emg-modal-msg");
  document.getElementById("emergency-modal").style.display = "flex";
}

async function submitEmergencyRequest() {
  clearMessage("emg-modal-msg");

  const bloodGroup = document.getElementById("emg-blood-group").value;
  const units = document.getElementById("emg-units").value;
  const purpose = document.getElementById("emg-purpose").value.trim();

  showLoading();
  const res = await apiPost(
    `/api/blood/request?bloodGroup=${bloodGroup}&units=${units}&priority=EMERGENCY&purpose=${encodeURIComponent(purpose)}`,
    null
  );
  hideLoading();

  if (res.data.success) {
    showMessage("emg-modal-msg", res.data.message, "success");
    setTimeout(async () => {
      closeModal("emergency-modal");
      await loadEmergencyRequests();
      await loadPendingRequests();
    }, 1000);
  } else {
    showMessage("emg-modal-msg", res.data.message || "Could not submit emergency request.", "error");
  }
}

function closeModal(id) {
  document.getElementById(id).style.display = "none";
}

/* ===================== DONORS ===================== */

async function loadDonors() {
  const res = await apiGet("/api/donors");
  const body = document.querySelector("#donors-table tbody");

  if (!res.data.success || !res.data.data || res.data.data.length === 0) {
    body.innerHTML = `<tr><td colspan="5" class="empty-row">No donors registered yet.</td></tr>`;
    document.getElementById("stat-donor-count").textContent = 0;
    return;
  }

  document.getElementById("stat-donor-count").textContent = res.data.data.length;

  body.innerHTML = res.data.data.map(d => `
    <tr>
      <td>${d.fullName}</td>
      <td>${formatBloodGroup(d.bloodGroup)}</td>
      <td>${d.mobile}</td>
      <td>${d.age}</td>
      <td><span class="status-pill ${d.isEligible ? "pill-green" : "pill-amber"}">${d.isEligible ? "Eligible" : "Not Eligible"}</span></td>
    </tr>
  `).join("");
}

async function registerDonor() {
  clearMessage("donor-msg");

  const fullName = document.getElementById("donor-name").value.trim();
  const bloodGroup = document.getElementById("donor-blood-group").value;
  const age = document.getElementById("donor-age").value;
  const mobile = document.getElementById("donor-mobile").value.trim();
  const address = document.getElementById("donor-address").value.trim();

  if (!fullName || !age || !mobile) {
    showMessage("donor-msg", "Please fill in Name, Age, and Mobile.", "error");
    return;
  }

  showLoading();
  const res = await apiPost("/api/donors/register", {
    fullName: fullName,
    bloodGroup: bloodGroup,
    age: parseInt(age, 10),
    mobile: mobile,
    address: address
  });
  hideLoading();

  if (res.data.success) {
    showMessage("donor-msg", res.data.message, "success");
    ["donor-name","donor-age","donor-mobile","donor-address"].forEach(id => document.getElementById(id).value = "");
    await loadDonors();
  } else {
    showMessage("donor-msg", res.data.message || "Could not register donor.", "error");
  }
}

/* ===================== HOSPITAL SEARCH ===================== */

async function searchHospitals() {
  const bloodGroup = document.getElementById("search-blood-group").value;
  const body = document.querySelector("#hospital-search-table tbody");

  body.innerHTML = `<tr><td colspan="4" class="empty-row">Searching...</td></tr>`;

  const res = await apiGet(`/api/blood/search?bloodGroup=${bloodGroup}`);

  if (!res.data.success || !res.data.data || res.data.data.length === 0) {
    body.innerHTML = `<tr><td colspan="4" class="empty-row">No results found.</td></tr>`;
    return;
  }

  body.innerHTML = res.data.data.map(r => `
    <tr>
      <td>${r.hospital}</td>
      <td>${formatBloodGroup(r.bloodGroup)}</td>
      <td>${r.units}</td>
      <td>${r.location}</td>
    </tr>
  `).join("");
}