/* ==========================================================
   MediDoc - analytics.js
   All logic for analytics.html
   Chart.js charts wired to real /api/analytics/* endpoints
   ========================================================== */

// Chart.js global defaults — clean look
Chart.defaults.font.family = "'Segoe UI', Arial, sans-serif";
Chart.defaults.font.size = 12;
Chart.defaults.color = "#6b7280";
Chart.defaults.plugins.legend.position = "bottom";

// Chart instances — kept so we can destroy before recreating
let bedChartInstance = null;
let bloodChartInstance = null;
let trendChartInstance = null;
let bloodDetailChartInstance = null;
let bedDetailChartInstance = null;

/* ===================== INIT ===================== */

document.addEventListener("DOMContentLoaded", async () => {
  const session = requireAuth(); // any role can view analytics
  if (!session) return;

  renderTodayDate();
  document.getElementById("analytics-avatar").textContent =
    getInitials(session.fullName);

  await loadOverview();
  await renderBedChart();
  await renderBloodChart();
  renderPatientTrendChart(); // simulated trend data
});

/* ===================== SECTION SWITCHING ===================== */

function showSection(name) {
  document.querySelectorAll(".section").forEach(s => s.classList.remove("active"));
  document.querySelectorAll(".smenu-item").forEach(m => m.classList.remove("active"));

  document.getElementById("section-" + name).classList.add("active");
  document.querySelector(`.smenu-item[data-section="${name}"]`).classList.add("active");

  const titles = {
    overview: "Hospital Analytics",
    blood: "Blood Analytics",
    beds: "Bed Analytics"
  };
  document.getElementById("page-title").textContent = titles[name] || "Analytics";

  // Render section-specific charts only when that tab is opened
  if (name === "blood") renderBloodDetailSection();
  if (name === "beds") renderBedDetailSection();
}

/* ===================== OVERVIEW STATS ===================== */

async function loadOverview() {
  const res = await apiGet("/api/analytics/summary");
  if (!res.data.success || !res.data.data) return;

  const d = res.data.data;
  document.getElementById("stat-patients").textContent = d.totalPatients ?? "--";
  document.getElementById("stat-doctors").textContent = d.activeDoctors ?? "--";
  document.getElementById("stat-beds").textContent = d.availableBeds ?? "--";
  document.getElementById("stat-total-beds").textContent = d.totalBeds ?? "--";
  document.getElementById("stat-appts").textContent = d.totalAppointments ?? "--";
}

/* ===================== BED OCCUPANCY DOUGHNUT ===================== */

async function renderBedChart() {
  const res = await apiGet("/api/analytics/bed-occupancy");
  if (!res.data.success || !res.data.data) return;

  const d = res.data.data;
  const available = d.available ?? 0;
  const occupied = d.occupied ?? 0;
  const maintenance = d.maintenance ?? 0;

  if (bedChartInstance) bedChartInstance.destroy();

  const ctx = document.getElementById("bedChart").getContext("2d");
  bedChartInstance = new Chart(ctx, {
    type: "doughnut",
    data: {
      labels: ["Available", "Occupied", "Maintenance"],
      datasets: [{
        data: [available, occupied, maintenance],
        backgroundColor: ["#3b6d11", "#e24b4a", "#854f0b"],
        borderColor: ["#eaf3de", "#fcebeb", "#faeeda"],
        borderWidth: 3
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { position: "bottom" },
        tooltip: {
          callbacks: {
            label: ctx => ` ${ctx.label}: ${ctx.parsed} beds`
          }
        }
      }
    }
  });
}

/* ===================== BLOOD INVENTORY BAR CHART ===================== */

async function renderBloodChart() {
  const res = await apiGet("/api/analytics/blood-levels");
  if (!res.data.success || !res.data.data) return;

  const inventory = res.data.data;
  const labels = inventory.map(b => formatBloodGroup(b.bloodGroup));
  const data = inventory.map(b => b.unitsAvailable ?? 0);
  const colors = data.map(u => u < 10 ? "#e24b4a" : "#185fa5");

  if (bloodChartInstance) bloodChartInstance.destroy();

  const ctx = document.getElementById("bloodChart").getContext("2d");
  bloodChartInstance = new Chart(ctx, {
    type: "bar",
    data: {
      labels: labels,
      datasets: [{
        label: "Units Available",
        data: data,
        backgroundColor: colors,
        borderRadius: 6,
        borderSkipped: false
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false },
        tooltip: {
          callbacks: {
            label: ctx => ` ${ctx.parsed.y} units`
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          grid: { color: "#f1f3f6" },
          ticks: { stepSize: 10 }
        },
        x: {
          grid: { display: false }
        }
      }
    }
  });
}

/* ===================== PATIENT TREND LINE CHART (simulated) ===================== */

function renderPatientTrendChart() {
  // Simulated daily registration data for last 7 days.
  // In production this would come from a backend endpoint
  // like GET /api/analytics/patient-trend?days=7
  const today = new Date();
  const labels = [];
  const months = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];

  for (let i = 6; i >= 0; i--) {
    const d = new Date(today);
    d.setDate(today.getDate() - i);
    labels.push(`${d.getDate()} ${months[d.getMonth()]}`);
  }

  // Simulated registration counts
  const data = [2, 5, 3, 7, 4, 8, 6];

  if (trendChartInstance) trendChartInstance.destroy();

  const ctx = document.getElementById("patientTrendChart").getContext("2d");
  trendChartInstance = new Chart(ctx, {
    type: "line",
    data: {
      labels: labels,
      datasets: [{
        label: "New Registrations",
        data: data,
        borderColor: "#185fa5",
        backgroundColor: "rgba(24, 95, 165, 0.08)",
        borderWidth: 2.5,
        pointBackgroundColor: "#185fa5",
        pointRadius: 5,
        tension: 0.4,
        fill: true
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false },
        tooltip: {
          callbacks: {
            label: ctx => ` ${ctx.parsed.y} new patients`
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          grid: { color: "#f1f3f6" },
          ticks: { stepSize: 2 }
        },
        x: {
          grid: { display: false }
        }
      }
    }
  });
}

/* ===================== BLOOD DETAIL SECTION ===================== */

async function renderBloodDetailSection() {
  const res = await apiGet("/api/analytics/blood-levels");
  if (!res.data.success || !res.data.data) return;

  const inventory = res.data.data;

  // Table
  const body = document.querySelector("#blood-detail-table tbody");
  body.innerHTML = inventory.map(b => {
    const units = b.unitsAvailable ?? 0;
    const isLow = units < 10;
    return `
      <tr>
        <td><strong>${formatBloodGroup(b.bloodGroup)}</strong></td>
        <td>${units}</td>
        <td>${b.unitsReserved ?? 0}</td>
        <td><span class="status-pill ${isLow ? "pill-red" : "pill-green"}">${isLow ? "Low Stock" : "Sufficient"}</span></td>
      </tr>
    `;
  }).join("");

  // Doughnut chart for blood distribution
  const labels = inventory.map(b => formatBloodGroup(b.bloodGroup));
  const data = inventory.map(b => b.unitsAvailable ?? 0);
  const colors = [
    "#e24b4a","#185fa5","#854f0b","#3b6d11",
    "#7c3aed","#0891b2","#db2777","#d97706"
  ];

  if (bloodDetailChartInstance) bloodDetailChartInstance.destroy();

  const ctx = document.getElementById("bloodDetailChart").getContext("2d");
  bloodDetailChartInstance = new Chart(ctx, {
    type: "doughnut",
    data: {
      labels: labels,
      datasets: [{
        data: data,
        backgroundColor: colors,
        borderWidth: 2,
        borderColor: "#fff"
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { position: "right" },
        tooltip: {
          callbacks: {
            label: ctx => ` ${ctx.label}: ${ctx.parsed} units`
          }
        }
      }
    }
  });
}

/* ===================== BED DETAIL SECTION ===================== */

async function renderBedDetailSection() {
  const res = await apiGet("/api/analytics/bed-occupancy");
  if (!res.data.success || !res.data.data) return;

  const d = res.data.data;
  const available = d.available ?? 0;
  const occupied = d.occupied ?? 0;
  const maintenance = d.maintenance ?? 0;

  document.getElementById("bed-available").textContent = available;
  document.getElementById("bed-occupied").textContent = occupied;
  document.getElementById("bed-maintenance").textContent = maintenance;

  if (bedDetailChartInstance) bedDetailChartInstance.destroy();

  const ctx = document.getElementById("bedDetailChart").getContext("2d");
  bedDetailChartInstance = new Chart(ctx, {
    type: "pie",
    data: {
      labels: ["Available", "Occupied", "Maintenance"],
      datasets: [{
        data: [available, occupied, maintenance],
        backgroundColor: ["#3b6d11", "#e24b4a", "#854f0b"],
        borderColor: ["#fff", "#fff", "#fff"],
        borderWidth: 3
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { position: "bottom" },
        tooltip: {
          callbacks: {
            label: ctx => ` ${ctx.label}: ${ctx.parsed} beds`
          }
        }
      }
    }
  });
}