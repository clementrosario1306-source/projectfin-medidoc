/* ==========================================================
   MediDoc - pharmacy.js
   All logic for pharmacy.html
   ========================================================== */

let allMedicines = []; // cached for client-side search filtering

document.addEventListener("DOMContentLoaded", async () => {
  const session = requireAuth(); // any staff role can view pharmacy
  if (!session) return;

  renderTodayDate();
  await loadMedicines();
});

/* ===================== SECTION SWITCHING ===================== */

function showSection(name) {
  document.querySelectorAll(".section").forEach(s => s.classList.remove("active"));
  document.querySelectorAll(".smenu-item").forEach(m => m.classList.remove("active"));

  document.getElementById("section-" + name).classList.add("active");
  document.querySelector(`.smenu-item[data-section="${name}"]`).classList.add("active");

  const titles = { stock: "Medicine Stock", issue: "Issue Medicine", add: "Add Medicine" };
  document.getElementById("page-title").textContent = titles[name] || "MediDoc";
}

/* ===================== MEDICINE STOCK TABLE ===================== */

async function loadMedicines() {
  const res = await apiGet("/api/pharmacy/stock");
  const body = document.querySelector("#medicines-table tbody");

  if (!res.data.success || !res.data.data || res.data.data.length === 0) {
    body.innerHTML = `<tr><td colspan="6" class="empty-row">No medicines found.</td></tr>`;
    document.getElementById("stat-total-medicines").textContent = 0;
    document.getElementById("stat-low-stock-meds").textContent = 0;
    return;
  }

  allMedicines = res.data.data;
  document.getElementById("stat-total-medicines").textContent = allMedicines.length;

  const lowStockCount = allMedicines.filter(m => m.stockQuantity <= m.minStockLevel).length;
  document.getElementById("stat-low-stock-meds").textContent = lowStockCount;

  renderMedicinesTable(allMedicines);
}

function renderMedicinesTable(medicines) {
  const body = document.querySelector("#medicines-table tbody");

  if (medicines.length === 0) {
    body.innerHTML = `<tr><td colspan="6" class="empty-row">No medicines match your search.</td></tr>`;
    return;
  }

  body.innerHTML = medicines.map(m => {
    const isLow = m.stockQuantity <= m.minStockLevel;
    return `
      <tr ${isLow ? 'style="background:#faeeda;"' : ""}>
        <td>${m.name} <span style="color:#9ca3af;font-size:11px;">(ID: ${m.id})</span></td>
        <td>${m.category || "--"}</td>
        <td>${m.stockQuantity}</td>
        <td>${m.minStockLevel}</td>
        <td>&#8377;${m.pricePerUnit ?? "--"}</td>
        <td><span class="status-pill ${isLow ? "pill-amber" : "pill-green"}">${isLow ? "Low Stock" : "In Stock"}</span></td>
      </tr>
    `;
  }).join("");
}

function filterMedicines() {
  const query = document.getElementById("med-search-box").value.toLowerCase().trim();
  if (!query) {
    renderMedicinesTable(allMedicines);
    return;
  }
  const filtered = allMedicines.filter(m =>
    m.name.toLowerCase().includes(query) ||
    (m.category && m.category.toLowerCase().includes(query))
  );
  renderMedicinesTable(filtered);
}

/* ===================== ADD STOCK ===================== */

async function addStock() {
  clearMessage("add-stock-msg");

  const medicineId = document.getElementById("stock-medicine-id").value;
  const quantity = document.getElementById("stock-quantity").value;

  if (!medicineId || !quantity) {
    showMessage("add-stock-msg", "Please enter Medicine ID and quantity.", "error");
    return;
  }

  showLoading();
  const res = await apiPost(`/api/pharmacy/add-stock?medicineId=${medicineId}&quantity=${quantity}`, null);
  hideLoading();

  if (res.data.success) {
    showMessage("add-stock-msg", res.data.message, "success");
    document.getElementById("stock-medicine-id").value = "";
    document.getElementById("stock-quantity").value = "";
    await loadMedicines();
  } else {
    showMessage("add-stock-msg", res.data.message || "Could not add stock.", "error");
  }
}

/* ===================== ISSUE MEDICINE ===================== */

async function issueMedicine() {
  clearMessage("issue-msg");

  const medicineId = document.getElementById("issue-medicine-id").value;
  const quantity = document.getElementById("issue-quantity").value;

  if (!medicineId || !quantity) {
    showMessage("issue-msg", "Please enter Medicine ID and quantity.", "error");
    return;
  }

  showLoading();
  const res = await apiPost(`/api/pharmacy/issue?medicineId=${medicineId}&quantity=${quantity}`, null);
  hideLoading();

  if (res.data.success) {
    showMessage("issue-msg", res.data.message, "success");
    document.getElementById("issue-medicine-id").value = "";
    document.getElementById("issue-quantity").value = "";
    await loadMedicines();
  } else {
    showMessage("issue-msg", res.data.message || "Could not issue medicine.", "error");
  }
}

/* ===================== ADD NEW MEDICINE ===================== */

async function addNewMedicine() {
  clearMessage("new-med-msg");

  const payload = {
    name: document.getElementById("new-med-name").value.trim(),
    genericName: document.getElementById("new-med-generic").value.trim(),
    category: document.getElementById("new-med-category").value.trim(),
    unit: document.getElementById("new-med-unit").value.trim(),
    stockQuantity: parseInt(document.getElementById("new-med-stock").value || "0", 10),
    minStockLevel: parseInt(document.getElementById("new-med-min").value || "10", 10),
    pricePerUnit: parseFloat(document.getElementById("new-med-price").value || "0"),
    manufacturer: document.getElementById("new-med-manufacturer").value.trim()
  };

  if (!payload.name) {
    showMessage("new-med-msg", "Please enter the medicine name.", "error");
    return;
  }

  showLoading();
  const res = await apiPost("/api/pharmacy/add-medicine", payload);
  hideLoading();

  if (res.data.success) {
    showMessage("new-med-msg", res.data.message, "success");
    ["new-med-name","new-med-generic","new-med-category","new-med-unit","new-med-stock","new-med-min","new-med-price","new-med-manufacturer"]
      .forEach(id => document.getElementById(id).value = "");
    await loadMedicines();
  } else {
    showMessage("new-med-msg", res.data.message || "Could not add medicine.", "error");
  }
}