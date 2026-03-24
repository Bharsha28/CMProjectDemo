const STORAGE_KEY = "cardmaster_session";
const LOGOUT_MESSAGE_KEY = "cardmaster_logout_message";

export function getStoredSession() {
  try {
    const rawValue = localStorage.getItem(STORAGE_KEY);
    return rawValue ? JSON.parse(rawValue) : null;
  } catch (error) {
    return null;
  }
}

export function saveSession(session) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
}

export function clearSession() {
  localStorage.removeItem(STORAGE_KEY);
}

export function saveLogoutMessage(message) {
  sessionStorage.setItem(LOGOUT_MESSAGE_KEY, message);
}

export function consumeLogoutMessage() {
  const message = sessionStorage.getItem(LOGOUT_MESSAGE_KEY) || "";
  sessionStorage.removeItem(LOGOUT_MESSAGE_KEY);
  return message;
}

export function getToken() {
  return getStoredSession()?.token || "";
}


export function isAuthenticated() {
  return Boolean(getToken());
}

export function getDefaultDashboardPath(role) {
  switch (role) {
    case "CUSTOMER":
      return "/customer";
    case "UNDERWRITER":
      return "/underwriter";
    case "OFFICER":
      return "/operations";
    case "ADMIN":
      return "/admin";
    case "RISK":
      return "/operations";
    default:
      return "/login";
  }
}
