const badgeMap = {
  Approved: "success",
  APPROVED: "success",
  Active: "success",
  ACTIVE: "success",
  Success: "success",
  COMPLETED: "success",
  Submitted: "primary",
  SUBMITTED: "primary",
  Open: "primary",
  OPEN: "primary",
  UnderReview: "warning",
  "Under Review": "warning",
  UNDER_REVIEW: "warning",
  Pending: "warning",
  PENDING: "warning",
  Conditional: "warning",
  CONDITIONAL: "warning",
  Issued: "info",
  ISSUE: "info",
  ISSUED: "info",
  Draft: "secondary",
  Closed: "secondary",
  CLOSED: "secondary",
  Rejected: "danger",
  REJECTED: "danger",
  Failed: "danger",
  FAILED: "danger",
  Reversed: "danger",
  Verified: "success",
  VERIFIED: "success",
  Blocked: "dark"
  ,
  BLOCKED: "dark",
  INACTIVE: "secondary",
  DISCONTINUED: "secondary"
};

function StatusBadge({ value }) {
  const color = badgeMap[value] || "secondary";
  return <span className={`badge text-bg-${color}`}>{value}</span>;
}

export default StatusBadge;
