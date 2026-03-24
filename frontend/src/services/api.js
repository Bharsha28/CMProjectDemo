import { clearSession, getStoredSession, getToken } from "../utils/auth";

const API_BASE_URL = "http://localhost:8082";


function getSessionEmail() {
  return getStoredSession()?.email || "";
}

function unwrapResponse(payload) {
  if (payload?.data) {
    return payload.data;
  }
  return payload;
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



export const customerApi = {
  getCustomers: async () => unwrapResponse(await fetchJson("/api/customers")),
  createCustomer: (payload) =>
    fetchJson("/api/customers", { method: "POST", body: JSON.stringify(payload) }),
  getMyCustomer: async () =>
    unwrapResponse(await fetchJson("/api/customers/my")),
  getApplications: async () => unwrapResponse(await fetchJson("/api/applications")),
  getMyApplications: async () => unwrapResponse(await fetchJson("/api/applications/my")),
  getApplicationsByCustomer: async (customerId) =>
    unwrapResponse(
      await fetchJson(`/api/applications/customer/${customerId}`)
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
  getProducts: async () => unwrapResponse(await fetchJson("/api/products")),
  getProductsStrict: async () =>
    unwrapResponse(await fetchJson("/api/products")),
  getMyCards: async () =>
    unwrapResponse(
      await fetchJson("/api/cards/my")
    ),
  getMyAccount: async () =>
    unwrapResponse(
      await fetchJson("/api/accounts/my")
    ),
  getMyTransactions: async () => {
    return unwrapResponse(await fetchJson("/api/transactions/my"));
  },
  updateMyCustomer: (payload) =>
    fetchJson("/api/customers/my", { method: "PUT", body: JSON.stringify(payload) }),
  getMyStatements: async (fromDate, toDate) => {
    let url = "/api/billing/statements/my";
    const params = new URLSearchParams();
    if (fromDate) params.append("fromDate", fromDate);
    if (toDate) params.append("toDate", toDate);
    if (params.toString()) url += `?${params.toString()}`;
    return unwrapResponse(await fetchJson(url));
  },
  getMyPayments: async () => {
    return unwrapResponse(await fetchJson("/api/billing/payments/my"));
  },
  capturePayment: (payload) =>
    fetchJson("/api/billing/payments/capture", { method: "POST", body: JSON.stringify(payload) })
};

export const underwriterApi = {
  getApplications: async () => unwrapResponse(await fetchJson("/api/applications")),
  getCreditScores: async () => unwrapResponse(await fetchJson("/api/scores")),
  createCreditScore: (payload) =>
    fetchJson(`/api/applications/${payload.applicationId}/scores`, {
      method: "POST",
      body: JSON.stringify(payload)
    }),
  getUnderwritingHistory: () => unwrapResponse(fetchJson("/api/decisions")),
  createDecision: (applicationId, payload) =>
    fetchJson(`/api/applications/${applicationId}/decisions`, {
      method: "POST",
      body: JSON.stringify(payload)
    })
};

export const operationsApi = {
  getCards: () => fetchJson("/api/cards"),
  createCard: (payload) =>
    fetchJson("/api/cards", { method: "POST", body: JSON.stringify(payload) }),
  getAccounts: () => fetchJson("/api/accounts"),
  createAccount: (payload) =>
    fetchJson("/api/accounts", { method: "POST", body: JSON.stringify(payload) }),
  getTransactions: () => fetchJson("/api/transactions"),
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
  getStatements: () => fetchJson("/api/billing/statements"),
  createStatement: (payload) =>
    fetchJson("/api/billing/statements/generate", {
      method: "POST",
      body: JSON.stringify(payload)
    }),
  closeStatement: (statementId) =>
    fetchJson(`/api/billing/statements/close/${statementId}`, {
      method: "POST"
    }),
  getPayments: () => fetchJson("/api/billing/payments"),
  createPayment: (payload) =>
    fetchJson("/api/billing/payments/capture", {
      method: "POST",
      body: JSON.stringify(payload)
    })
};

export const adminApi = {
  getUsers: async () => unwrapResponse(await fetchJson("/api/users")),
  registerUser: (payload) =>
    fetchJson("/api/users/register", { method: "POST", body: JSON.stringify(payload) }),
  getProducts: () => fetchJson("/api/products"),
  createProduct: (payload) =>
    fetchJson("/api/products", { method: "POST", body: JSON.stringify(payload) }),
  getFees: () => fetchJson("/api/fees"),
  createFee: (payload) =>
    fetchJson("/api/fees", { method: "POST", body: JSON.stringify(payload) }),
  getAuditLogs: async () => unwrapResponse(await fetchJson("/api/auditlogs")),
  getRecentTransactions: async () => unwrapResponse(await fetchJson("/api/transactions/recent"))
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
