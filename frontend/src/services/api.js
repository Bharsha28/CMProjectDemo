import {
  accounts,
  applications,
  auditLogs,
  cardProducts,
  cards,
  creditScores,
  customerProfiles,
  feeConfigs,
  payments,
  statements,
  transactions,
  underwritingHistory,
  users
} from "../data/mockData";
import { clearSession, getStoredSession, getToken } from "../utils/auth";

const API_BASE_URL = "http://localhost:8082";


function getSessionEmail() {
  return getStoredSession()?.email || "";
}

function getMockCustomerProfile() {
  const session = getStoredSession();
  const email = session?.email || customerProfiles[0]?.email || "";
  const existingCustomer = customerProfiles.find(
    (item) => (item.email || item.contactInfo?.email) === email
  );

  if (existingCustomer) {
    return existingCustomer;
  }

  return {
    customerId: 9001,
    name: session?.name || "Demo Customer",
    dob: "1995-01-01",
    contactInfo: {
      address: "Demo Address",
      email,
      phone: "9876543210"
    },
    income: 500000,
    employmentType: "Salaried",
    status: "Active"
  };
}

function unwrapResponse(payload, fallback) {
  if (payload?.data) {
    return payload.data;
  }

  return payload ?? fallback;
}

function decodeJwtPayload(token) {
  try {
    const encoded = token.split(".")[1];
    if (!encoded) {
      return {};
    }

    return JSON.parse(atob(encoded));
  } catch (error) {
    return {};
  }
}

async function fetchJson(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(getToken() ? { Authorization: getToken() } : {}),
      ...options.headers
    },
    ...options
  });

  if (!response.ok) {
    let errorMessage = `Request failed for ${path}`;

    try {
      const errorPayload = await response.json();
      errorMessage = errorPayload?.msg || errorPayload?.message || errorMessage;
    } catch (error) {
      errorMessage = `${errorMessage} (${response.status})`;
    }

    throw new Error(errorMessage);
  }

  return response.json();
}

async function safeFetch(path, fallback, options = {}) {
  try {
    return await fetchJson(path, options);
  } catch (error) {
    return fallback;
  }
}

export const customerApi = {
  getCustomers: async () => unwrapResponse(await safeFetch("/api/customers", { data: customerProfiles }), customerProfiles),
  createCustomer: (payload) =>
    fetchJson("/api/customers", { method: "POST", body: JSON.stringify(payload) }),
  getMyCustomer: async () =>
    unwrapResponse(await safeFetch("/api/customers/my", { data: customerProfiles[0] }), customerProfiles[0]),
  getApplications: async () => unwrapResponse(await safeFetch("/api/applications", { data: applications }), applications),
  getApplicationsByCustomer: async (customerId) =>
    unwrapResponse(
      await safeFetch(`/api/applications/customer/${customerId}`, { data: applications.filter((item) => item.customerId === customerId) }),
      applications.filter((item) => item.customerId === customerId)
    ),
  createApplication: async (payload) =>
    fetchJson("/api/applications", { method: "POST", body: JSON.stringify(payload) }),
  uploadDocument: async (payload) =>
    fetchJson("/api/documents", { method: "POST", body: JSON.stringify(payload) }),
  uploadDocumentFile: async (applicationId, documentType, file) => {
    const formData = new FormData();
    formData.append("applicationId", applicationId);
    formData.append("documentType", documentType);
    formData.append("file", file);

    const response = await fetch(`${API_BASE_URL}/api/documents/upload`, {
      method: "POST",
      headers: {
        ...(getToken() ? { Authorization: getToken() } : {})
      },
      body: formData
    });

    if (!response.ok) {
      throw new Error("File upload failed");
    }
    return response.json();
  },
  getProducts: async () => unwrapResponse(await safeFetch("/api/products", cardProducts), cardProducts),
  getProductsStrict: async () =>
    unwrapResponse(await fetchJson("/api/products"), []),
  getMyCards: async () =>
    unwrapResponse(
      await safeFetch("/api/cards/my", cards.filter((item) => item.customerEmail === getSessionEmail())),
      cards.filter((item) => item.customerEmail === getSessionEmail())
    ),
  getMyAccount: async () =>
    unwrapResponse(
      await safeFetch("/api/accounts/my", accounts.find((item) => item.customerEmail === getSessionEmail()) || accounts[0]),
      accounts.find((item) => item.customerEmail === getSessionEmail()) || accounts[0]
    ),
  getMyTransactions: async () => {
    const account = accounts.find((item) => item.customerEmail === getSessionEmail());
    const fallbackRows = account
      ? transactions.filter((item) => item.accountId === account.accountId)
      : [];
    return unwrapResponse(await safeFetch("/api/transactions/my", fallbackRows), fallbackRows);
  },
  updateMyCustomer: (payload) =>
    fetchJson("/api/customers/my", { method: "PUT", body: JSON.stringify(payload) }),
  getMyStatements: async () => {
    const account = accounts.find((item) => item.customerEmail === getSessionEmail());
    const fallbackRows = account
      ? statements.filter((item) => item.accountId === account.accountId)
      : [];
    return unwrapResponse(await safeFetch("/api/billing/statements/my", fallbackRows), fallbackRows);
  },
  getMyPayments: async () => {
    const account = accounts.find((item) => item.customerEmail === getSessionEmail());
    const fallbackRows = account
      ? payments.filter((item) => item.accountId === account.accountId)
      : [];
    return unwrapResponse(await safeFetch("/api/billing/payments/my", fallbackRows), fallbackRows);
  },
  capturePayment: (payload) =>
    fetchJson("/api/billing/payments/capture", { method: "POST", body: JSON.stringify(payload) })
};

export const underwriterApi = {
  getApplications: async () => unwrapResponse(await safeFetch("/api/applications", { data: applications }), applications),
  getCreditScores: () => Promise.resolve(creditScores),
  getUnderwritingHistory: () => Promise.resolve(underwritingHistory),
  createDecision: (applicationId, payload) =>
    fetchJson(`/api/applications/${applicationId}/decisions`, {
      method: "POST",
      body: JSON.stringify(payload)
    })
};

export const operationsApi = {
  getCards: () => safeFetch("/api/cards/my", cards),
  createCard: (payload) =>
    fetchJson("/api/cards", { method: "POST", body: JSON.stringify(payload) }),
  getAccounts: () => safeFetch("/api/accounts/my", accounts),
  createAccount: (payload) =>
    fetchJson("/api/accounts", { method: "POST", body: JSON.stringify(payload) }),
  getTransactions: () => safeFetch("/api/transactions", transactions),
  authorizeTransaction: (payload) =>
    fetchJson("/api/transactions/authorize", {
      method: "POST",
      body: JSON.stringify(payload)
    }),
  postTransaction: (transactionId) =>
    fetchJson(`/api/transactions/post/${transactionId}`, {
      method: "POST"
    }),
  reverseTransaction: (transactionId) =>
    fetchJson(`/api/transactions/reverse/${transactionId}`, {
      method: "POST"
    }),
  getStatements: () => safeFetch("/api/billing/statements", statements),
  createStatement: (payload) =>
    fetchJson("/api/billing/statements/generate", {
      method: "POST",
      body: JSON.stringify(payload)
    }),
  closeStatement: (statementId) =>
    fetchJson(`/api/billing/statements/close/${statementId}`, {
      method: "POST"
    }),
  getPayments: () => safeFetch("/api/billing/payments", payments),
  createPayment: (payload) =>
    fetchJson("/api/billing/payments/capture", {
      method: "POST",
      body: JSON.stringify(payload)
    })
};

export const adminApi = {
  getUsers: async () => unwrapResponse(await safeFetch("/api/users", { data: users }), users),
  registerUser: (payload) =>
    fetchJson("/api/users/register", { method: "POST", body: JSON.stringify(payload) }),
  getProducts: () => safeFetch("/api/products", cardProducts),
  createProduct: (payload) =>
    fetchJson("/api/products", { method: "POST", body: JSON.stringify(payload) }),
  getFees: () => Promise.resolve(feeConfigs),
  createFee: (payload) =>
    fetchJson("/api/fees", { method: "POST", body: JSON.stringify(payload) }),
  getAuditLogs: async () => unwrapResponse(await safeFetch("/api/auditlogs", { data: auditLogs }), auditLogs)
};

export const authApi = {
  async login(payload) {
    const response = await fetchJson("/api/users/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    });

    const token = response?.data || "";
    const tokenPayload = decodeJwtPayload(token.replace("Bearer ", ""));
    const currentSession = getStoredSession();

    return {
      token,
      email: payload.email,
      role: tokenPayload?.role || currentSession?.role || "",
      name: tokenPayload?.sub || currentSession?.name || payload.email,
      isMockLogin: false
    };
  },
  register: (payload) =>
    fetchJson("/api/users/register", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    }),
  async logout() {
    try {
      const response = await fetchJson("/api/users/logout", {
        method: "POST"
      });
      return response?.data || response?.msg || "Logout successful.";
    } finally {
      clearSession();
    }
  }
};
