export const productOptions = [
  { id: 1, name: "Classic Credit Card", category: "Standard" },
  { id: 2, name: "Travel Rewards Card", category: "Gold" },
  { id: 3, name: "Signature Platinum Card", category: "Platinum" }
];

export const customerStats = [
  { label: "Open Applications", value: "03", icon: "bi-file-earmark-text" },
  { label: "KYC Pending", value: "01", icon: "bi-person-vcard" },
  { label: "Approved Cards", value: "02", icon: "bi-credit-card-2-front" },
  { label: "Rewards Balance", value: "12,850", icon: "bi-stars" }
];

export const customerProfiles = [
  {
    customerId: 1001,
    name: "Ananya Rao",
    dob: "1996-04-11",
    email: "ananya.rao@example.com",
    phone: "+91 98765 43210",
    income: 920000,
    employmentType: "Salaried",
    status: "Active"
  },
  {
    customerId: 1002,
    name: "Rohan Mehta",
    dob: "1992-09-22",
    email: "rohan.mehta@example.com",
    phone: "+91 91234 56780",
    income: 1450000,
    employmentType: "Self-Employed",
    status: "Under Review"
  }
];

export const applications = [
  {
    applicationId: 5001,
    customerId: 1001,
    customerName: "Ananya Rao",
    customerEmail: "ananya.rao@example.com",
    productId: 2,
    productName: "Travel Rewards Card",
    requestedLimit: 300000,
    applicationDate: "2026-03-10",
    status: "SUBMITTED"
  },
  {
    applicationId: 5002,
    customerId: 1002,
    customerName: "Rohan Mehta",
    customerEmail: "rohan.mehta@example.com",
    productId: 3,
    productName: "Signature Platinum Card",
    requestedLimit: 600000,
    applicationDate: "2026-03-14",
    status: "UNDER_REVIEW"
  },
  {
    applicationId: 5003,
    customerId: 1001,
    customerName: "Ananya Rao",
    customerEmail: "ananya.rao@example.com",
    productId: 1,
    productName: "Classic Credit Card",
    requestedLimit: 150000,
    applicationDate: "2026-03-18",
    status: "APPROVED"
  }
];

export const creditScores = [
  {
    applicationId: 5001,
    customerName: "Ananya Rao",
    customerEmail: "ananya.rao@example.com",
    bureauScore: 762,
    internalScore: 81,
    riskBand: "Low",
    generatedDate: "2026-03-12"
  },
  {
    applicationId: 5002,
    customerName: "Rohan Mehta",
    customerEmail: "rohan.mehta@example.com",
    bureauScore: 689,
    internalScore: 67,
    riskBand: "Medium",
    generatedDate: "2026-03-16"
  }
];

export const underwritingHistory = [
  {
    decisionId: 9001,
    applicationId: 5003,
    customerEmail: "ananya.rao@example.com",
    underwriter: "Priya Sharma",
    decision: "Approved",
    approvedLimit: 175000,
    remarks: "Stable salary and low utilization.",
    decisionDate: "2026-03-19"
  },
  {
    decisionId: 9002,
    applicationId: 5002,
    customerEmail: "rohan.mehta@example.com",
    underwriter: "Rahul Nair",
    decision: "Conditional",
    approvedLimit: 350000,
    remarks: "Need latest bank statement before final issue.",
    decisionDate: "2026-03-20"
  }
];

export const cards = [
  {
    cardId: 7001,
    customerId: 1001,
    customerName: "Ananya Rao",
    customerEmail: "ananya.rao@example.com",
    productName: "Travel Rewards Card",
    maskedCardNumber: "4567 XXXX XXXX 9812",
    expiryDate: "2029-08",
    status: "ACTIVE"
  },
  {
    cardId: 7002,
    customerId: 1002,
    customerName: "Rohan Mehta",
    customerEmail: "rohan.mehta@example.com",
    productName: "Signature Platinum Card",
    maskedCardNumber: "4567 XXXX XXXX 2211",
    expiryDate: "2029-12",
    status: "ISSUED"
  }
];

export const accounts = [
  {
    accountId: 8101,
    cardId: 7001,
    customerName: "Ananya Rao",
    customerEmail: "ananya.rao@example.com",
    creditLimit: 300000,
    availableLimit: 212000,
    openDate: "2026-01-05",
    status: "ACTIVE"
  },
  {
    accountId: 8102,
    cardId: 7002,
    customerName: "Rohan Mehta",
    customerEmail: "rohan.mehta@example.com",
    creditLimit: 350000,
    availableLimit: 350000,
    openDate: "2026-03-21",
    status: "ACTIVE"
  }
];

export const transactions = [
  {
    transactionId: 10001,
    accountId: 8101,
    amount: 18450,
    currency: "INR",
    merchant: "SkyJet Airlines",
    channel: "Online",
    transactionDate: "2026-03-15",
    status: "Posted"
  },
  {
    transactionId: 10002,
    accountId: 8101,
    amount: 2200,
    currency: "INR",
    merchant: "Fuel Point",
    channel: "POS",
    transactionDate: "2026-03-17",
    status: "Posted"
  },
  {
    transactionId: 10003,
    accountId: 8102,
    amount: 9100,
    currency: "INR",
    merchant: "TechStore",
    channel: "Online",
    transactionDate: "2026-03-18",
    status: "Failed"
  },
  {
    transactionId: 10004,
    accountId: 8102,
    amount: 4500,
    currency: "INR",
    merchant: "CityMart",
    channel: "POS",
    transactionDate: "2026-03-19",
    status: "Reversed"
  }
];

export const statements = [
  {
    statementId: 6001,
    accountId: 8101,
    customerName: "Ananya Rao",
    customerEmail: "ananya.rao@example.com",
    periodStart: "2026-02-01",
    periodEnd: "2026-02-28",
    totalDue: 24850,
    minimumDue: 3000,
    generatedDate: "2026-03-01",
    status: "OPEN"
  },
  {
    statementId: 6002,
    accountId: 8102,
    customerName: "Rohan Mehta",
    customerEmail: "rohan.mehta@example.com",
    periodStart: "2026-02-01",
    periodEnd: "2026-02-28",
    totalDue: 0,
    minimumDue: 0,
    generatedDate: "2026-03-01",
    status: "CLOSED"
  }
];

export const payments = [
  {
    paymentId: 3001,
    accountId: 8101,
    customerName: "Ananya Rao",
    customerEmail: "ananya.rao@example.com",
    amount: 12000,
    paymentDate: "2026-03-09",
    method: "UPI",
    status: "COMPLETED"
  },
  {
    paymentId: 3002,
    accountId: 8101,
    customerName: "Ananya Rao",
    customerEmail: "ananya.rao@example.com",
    amount: 5000,
    paymentDate: "2026-03-16",
    method: "NETBANKING",
    status: "COMPLETED"
  },
  {
    paymentId: 3003,
    accountId: 8102,
    customerName: "Rohan Mehta",
    customerEmail: "rohan.mehta@example.com",
    amount: 4500,
    paymentDate: "2026-03-20",
    method: "CHEQUE",
    status: "PENDING"
  }
];

export const users = [
  {
    userId: 1,
    name: "Bank Admin",
    email: "admin@cardmaster.com",
    phone: "9000011111",
    role: "ADMIN"
  },
  {
    userId: 2,
    name: "Priya Sharma",
    email: "underwriter@cardmaster.com",
    phone: "9000022222",
    role: "UNDERWRITER"
  },
  {
    userId: 3,
    name: "Kiran Ops",
    email: "ops@cardmaster.com",
    phone: "9000033333",
    role: "OFFICER"
  }
];

export const cardProducts = [
  {
    productId: 1,
    name: "Classic Credit Card",
    category: "Standard",
    interestRate: 29.99,
    annualFee: 499,
    status: "Active"
  },
  {
    productId: 2,
    name: "Travel Rewards Card",
    category: "Gold",
    interestRate: 26.5,
    annualFee: 1499,
    status: "ACTIVE"
  },
  {
    productId: 3,
    name: "Signature Platinum Card",
    category: "Platinum",
    interestRate: 24.0,
    annualFee: 3999,
    status: "ISSUE"
  }
];

export const feeConfigs = [
  { feeId: 101, productId: 1, productName: "Classic Credit Card", feeType: "LATE_PAYMENT", amount: 750 },
  { feeId: 102, productId: 2, productName: "Travel Rewards Card", feeType: "OVERLIMIT", amount: 1200 },
  { feeId: 103, productId: 3, productName: "Signature Platinum Card", feeType: "ANNUAL", amount: 3999 }
];

export const auditLogs = [
  {
    auditId: 1,
    userEmail: "admin@cardmaster.com",
    action: "Created card product",
    resource: "Travel Rewards Card",
    timestamp: "2026-03-18 10:15 AM"
  },
  {
    auditId: 2,
    userEmail: "underwriter@cardmaster.com",
    action: "Conditional approval",
    resource: "Application 5002",
    timestamp: "2026-03-20 02:45 PM"
  },
  {
    auditId: 3,
    userEmail: "ops@cardmaster.com",
    action: "Generated statement",
    resource: "Account 8101",
    timestamp: "2026-03-21 11:05 AM"
  }
];
