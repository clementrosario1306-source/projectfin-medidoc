/* ==========================================================
   MediDoc - api.js
   Reusable fetch() helper used across every page.
   ========================================================== */

// IMPORTANT: change this if your Spring Boot server runs on a
// different port. Default Spring Boot port is 8080.
const API_BASE_URL = "http://localhost:8080";

/**
 * Generic API call function.
 * @param {string} endpoint - e.g. "/api/patients/register"
 * @param {string} method   - GET, POST, PUT, DELETE
 * @param {object|null} body - request body (will be JSON.stringify'd)
 * @param {boolean} useAuth - if true, attaches JWT token from localStorage
 */
async function apiCall(endpoint, method = "GET", body = null, useAuth = true) {
  const headers = {
    "Content-Type": "application/json"
  };

  if (useAuth) {
    const token = localStorage.getItem("medidoc_token");
    if (token) {
      headers["Authorization"] = "Bearer " + token;
    }
  }

  const options = {
    method: method,
    headers: headers
  };

  if (body !== null) {
    options.body = JSON.stringify(body);
  }

  try {
    const response = await fetch(API_BASE_URL + endpoint, options);
    const data = await response.json();
    return { ok: response.ok, status: response.status, data: data };
  } catch (err) {
    // Server not running, CORS issue, or network failure
    return {
      ok: false,
      status: 0,
      data: { success: false, message: "Could not connect to MediDoc server. Is it running on " + API_BASE_URL + " ?" }
    };
  }
}

// Convenience wrappers
const apiGet  = (endpoint, useAuth = true) => apiCall(endpoint, "GET", null, useAuth);
const apiPost = (endpoint, body, useAuth = true) => apiCall(endpoint, "POST", body, useAuth);
const apiPut  = (endpoint, body, useAuth = true) => apiCall(endpoint, "PUT", body, useAuth);
const apiDelete = (endpoint, useAuth = true) => apiCall(endpoint, "DELETE", null, useAuth);

/* ===================== SESSION HELPERS ===================== */

function saveSession(token, role, uniqueId, fullName) {
  localStorage.setItem("medidoc_token", token);
  localStorage.setItem("medidoc_role", role);
  localStorage.setItem("medidoc_id", uniqueId);
  localStorage.setItem("medidoc_name", fullName);
}

function clearSession() {
  localStorage.removeItem("medidoc_token");
  localStorage.removeItem("medidoc_role");
  localStorage.removeItem("medidoc_id");
  localStorage.removeItem("medidoc_name");
}

function getSession() {
  return {
    token: localStorage.getItem("medidoc_token"),
    role: localStorage.getItem("medidoc_role"),
    uniqueId: localStorage.getItem("medidoc_id"),
    fullName: localStorage.getItem("medidoc_name")
  };
}

/* ===================== UI HELPERS ===================== */

function showLoading() {
  const el = document.getElementById("loading-overlay");
  if (el) el.style.display = "flex";
}

function hideLoading() {
  const el = document.getElementById("loading-overlay");
  if (el) el.style.display = "none";
}

function showMessage(elementId, text, type) {
  const el = document.getElementById(elementId);
  if (!el) return;
  el.textContent = text;
  el.className = "form-msg show " + type; // type: error | success | info
}

function clearMessage(elementId) {
  const el = document.getElementById(elementId);
  if (!el) return;
  el.className = "form-msg";
  el.textContent = "";
}