/* ==========================================================
   MediDoc - auth.js
   Handles: role switching, OTP send, patient login,
            staff login, registration, redirect after login.
   ========================================================== */

let lastRegisteredId = "";

/* ===================== ROLE / CARD SWITCHING ===================== */

function switchRole(role) {
  const patientTab = document.getElementById("tab-patient");
  const staffTab = document.getElementById("tab-staff");
  const patientForm = document.getElementById("patient-login-form");
  const staffForm = document.getElementById("staff-login-form");

  if (role === "patient") {
    patientTab.classList.add("active");
    staffTab.classList.remove("active");
    patientForm.style.display = "block";
    staffForm.style.display = "none";
  } else {
    staffTab.classList.add("active");
    patientTab.classList.remove("active");
    staffForm.style.display = "block";
    patientForm.style.display = "none";
  }
}

function showLogin() {
  document.getElementById("login-card").style.display = "block";
  document.getElementById("register-card").style.display = "none";
  document.getElementById("success-card").style.display = "none";
}

function showRegister() {
  document.getElementById("login-card").style.display = "none";
  document.getElementById("register-card").style.display = "block";
  document.getElementById("success-card").style.display = "none";
}

function showSuccess(uniqueId) {
  document.getElementById("login-card").style.display = "none";
  document.getElementById("register-card").style.display = "none";
  document.getElementById("success-card").style.display = "block";
  document.getElementById("generated-id").textContent = uniqueId;
}

function goToLoginWithId() {
  showLogin();
  switchRole("patient");
  document.getElementById("p-unique-id").value = lastRegisteredId;
}

/* ===================== VALIDATION HELPERS ===================== */

function isValidMobile(mobile) {
  return /^[6-9]\d{9}$/.test(mobile);
}

/* ===================== SEND OTP ===================== */

async function sendOtp() {
  clearMessage("patient-msg");

  const uniqueId = document.getElementById("p-unique-id").value.trim();
  const mobile = document.getElementById("p-mobile").value.trim();

  if (!uniqueId) {
    showMessage("patient-msg", "Please enter your Unique Health ID.", "error");
    return;
  }
  if (!isValidMobile(mobile)) {
    showMessage("patient-msg", "Please enter a valid 10-digit mobile number.", "error");
    return;
  }

  const btn = document.getElementById("send-otp-btn");
  btn.disabled = true;
  btn.textContent = "Sending...";

  const res = await apiPost("/api/auth/send-otp", {
    uniqueId: uniqueId,
    mobileNumber: mobile
  }, false);

  btn.disabled = false;
  btn.textContent = "Send OTP";

  if (res.data.success) {
    showMessage("patient-msg", res.data.message, "success");
  } else {
    showMessage("patient-msg", res.data.message || "Failed to send OTP.", "error");
  }
}

/* ===================== PATIENT LOGIN ===================== */

async function patientLogin() {
  clearMessage("patient-msg");

  const uniqueId = document.getElementById("p-unique-id").value.trim();
  const mobile = document.getElementById("p-mobile").value.trim();
  const otp = document.getElementById("p-otp").value.trim();

  if (!uniqueId || !mobile || !otp) {
    showMessage("patient-msg", "Please fill Unique ID, mobile number, and OTP.", "error");
    return;
  }

  const btn = document.getElementById("patient-login-btn");
  btn.disabled = true;
  btn.textContent = "Logging in...";
  showLoading();

  const res = await apiPost("/api/auth/patient-login", {
    uniqueId: uniqueId,
    mobileNumber: mobile,
    otp: otp
  }, false);

  hideLoading();
  btn.disabled = false;
  btn.textContent = "Login as Patient";

  if (res.data.success) {
    showMessage("patient-msg", res.data.message, "success");
    saveSession(res.data.token, res.data.role, res.data.uniqueId, res.data.fullName);
    setTimeout(() => {
      window.location.href = "pages/patient-dashboard.html";
    }, 700);
  } else {
    showMessage("patient-msg", res.data.message || "Login failed. Please check your details.", "error");
  }
}

/* ===================== STAFF LOGIN ===================== */

async function staffLogin() {
  clearMessage("staff-msg");

  const employeeId = document.getElementById("s-employee-id").value.trim();
  const password = document.getElementById("s-password").value;
  const role = document.getElementById("s-role").value;

  if (!employeeId || !password) {
    showMessage("staff-msg", "Please enter your Employee ID and password.", "error");
    return;
  }

  const btn = document.getElementById("staff-login-btn");
  btn.disabled = true;
  btn.textContent = "Logging in...";
  showLoading();

  const res = await apiPost("/api/auth/staff-login", {
    employeeId: employeeId,
    password: password,
    role: role
  }, false);

  hideLoading();
  btn.disabled = false;
  btn.textContent = "Login as Staff";

  if (res.data.success) {
    showMessage("staff-msg", res.data.message, "success");
    saveSession(res.data.token, res.data.role, res.data.uniqueId, res.data.fullName);

    setTimeout(() => {
      const userRole = res.data.role;
      if (userRole === "DOCTOR") {
        window.location.href = "pages/doctor-dashboard.html";
      } else if (userRole === "ADMIN") {
        window.location.href = "pages/admin-dashboard.html";
      } else if (userRole === "RECEPTIONIST") {
        window.location.href = "pages/receptionist-dashboard.html";
      } else if (userRole === "BLOOD_BANK_OFFICER") {
        window.location.href = "pages/blood-bank.html";
      } else {
        window.location.href = "pages/admin-dashboard.html";
      }
    }, 700);
  } else {
    showMessage("staff-msg", res.data.message || "Login failed. Please check your credentials.", "error");
  }
}

/* ===================== PATIENT REGISTRATION ===================== */

async function registerPatient() {
  clearMessage("register-msg");

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

  if (bloodGroup) {
    payload.bloodGroup = bloodGroup;
  }

  const btn = document.getElementById("register-btn");
  btn.disabled = true;
  btn.textContent = "Registering...";
  showLoading();

  const res = await apiPost("/api/patients/register", payload, false);

  hideLoading();
  btn.disabled = false;
  btn.textContent = "Register";

  if (res.data.success) {
    const uniqueId = res.data.data ? res.data.data.uniqueId : "";
    lastRegisteredId = uniqueId;
    showSuccess(uniqueId);
  } else {
    showMessage("register-msg", res.data.message || "Registration failed. Please try again.", "error");
  }
}

/* ===================== LOAD HOSPITAL STATS (for left panel) ===================== */

async function loadStats() {
  const res = await apiGet("/api/analytics/summary", false);
  if (res.data && res.data.success && res.data.data) {
    const d = res.data.data;
    document.getElementById("stat-patients").textContent = d.totalPatients ?? "--";
    document.getElementById("stat-doctors").textContent = d.totalDoctors ?? "--";
    document.getElementById("stat-beds").textContent = d.totalBeds ?? "--";
  }
}

/* ===================== INIT ===================== */

document.addEventListener("DOMContentLoaded", () => {
  loadStats();
});

function redirectToDashboard(role) {
  if (role === "PATIENT") window.location.href = "pages/patient-dashboard.html";
  else if (role === "DOCTOR") window.location.href = "pages/doctor-dashboard.html";
  else if (role === "ADMIN") window.location.href = "pages/admin-dashboard.html";
  else if (role === "RECEPTIONIST") window.location.href = "pages/receptionist-dashboard.html";
  else if (role === "BLOOD_BANK_OFFICER") window.location.href = "pages/blood-bank.html";
}