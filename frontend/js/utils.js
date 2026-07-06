/* ==========================================================
   MediDoc - utils.js
   Small shared helpers used across every dashboard page.
   ========================================================== */

/**
 * Formats blood group enum value (e.g. "B_POSITIVE") into
 * the familiar short form (e.g. "B+").
 */
function formatBloodGroup(bg) {
  if (!bg) return "--";
  const map = {
    A_POSITIVE: "A+", A_NEGATIVE: "A-",
    B_POSITIVE: "B+", B_NEGATIVE: "B-",
    O_POSITIVE: "O+", O_NEGATIVE: "O-",
    AB_POSITIVE: "AB+", AB_NEGATIVE: "AB-"
  };
  return map[bg] || bg;
}

/**
 * Formats an ISO date/datetime string into a readable form,
 * e.g. "2026-06-20" -> "20 Jun 2026"
 */
function formatDate(isoString) {
  if (!isoString) return "--";
  const d = new Date(isoString);
  if (isNaN(d.getTime())) return isoString;
  const months = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];
  return `${d.getDate()} ${months[d.getMonth()]} ${d.getFullYear()}`;
}

/**
 * Formats a datetime string into date + time,
 * e.g. "10:32 AM, 20 Jun 2026"
 */
function formatDateTime(isoString) {
  if (!isoString) return "--";
  const d = new Date(isoString);
  if (isNaN(d.getTime())) return isoString;
  let hours = d.getHours();
  const minutes = d.getMinutes().toString().padStart(2, "0");
  const ampm = hours >= 12 ? "PM" : "AM";
  hours = hours % 12 || 12;
  return `${hours}:${minutes} ${ampm}, ${formatDate(isoString)}`;
}

/** Returns today's date formatted, used in topbars. */
function getTodayFormatted() {
  return formatDate(new Date().toISOString());
}

/** Maps a status string to a CSS pill class. */
function statusPillClass(status) {
  if (!status) return "pill-gray";
  const s = status.toUpperCase();
  if (["BOOKED", "PENDING", "REQUESTED", "ACTIVE", "AVAILABLE"].includes(s)) return "pill-blue";
  if (["COMPLETED", "ISSUED", "APPROVED", "ACCEPTED"].includes(s)) return "pill-green";
  if (["IN_PROGRESS", "WAITING", "ASSIGNED", "ON_DUTY", "MEDIUM"].includes(s)) return "pill-amber";
  if (["CANCELLED", "REJECTED", "NO_SHOW", "EMERGENCY", "HIGH", "CRITICAL"].includes(s)) return "pill-red";
  return "pill-gray";
}

/** Converts a status enum like "IN_PROGRESS" into "In Progress". */
function humanizeStatus(status) {
  if (!status) return "--";
  return status
    .toLowerCase()
    .split("_")
    .map(w => w.charAt(0).toUpperCase() + w.slice(1))
    .join(" ");
}

/** Returns a CSS class for risk level badges. */
function riskTagClass(risk) {
  if (!risk) return "tag-risk-low";
  const r = risk.toUpperCase();
  if (r === "HIGH") return "tag-risk-high";
  if (r === "MEDIUM") return "tag-risk-medium";
  return "tag-risk-low";
}

/** Returns initials from a full name, e.g. "Clement Rosario" -> "CR" */
function getInitials(name) {
  if (!name) return "--";
  const parts = name.trim().split(/\s+/);
  if (parts.length === 1) return parts[0].substring(0, 2).toUpperCase();
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}

/** Guard: redirect to login if no session token is found. */
function requireAuth(expectedRole) {
  const session = getSession();
  if (!session.token) {
    window.location.href = "../index.html";
    return null;
  }
  if (expectedRole && session.role !== expectedRole) {
    // Logged in but wrong role for this page - send back to login
    window.location.href = "../index.html";
    return null;
  }
  return session;
}

/** Logs the user out and returns to the login screen. */
function logout() {
  clearSession();
  window.location.href = "../index.html";
}

/** Sets today's date into any element with id="today-date". */
function renderTodayDate() {
  const el = document.getElementById("today-date");
  if (el) el.textContent = getTodayFormatted();
}