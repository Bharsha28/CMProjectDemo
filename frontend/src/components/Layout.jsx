import { Link, NavLink, useNavigate } from "react-router-dom";
import { authApi } from "../services/api";
import { clearSession, getStoredSession, saveLogoutMessage } from "../utils/auth";

const navSections = {
  customer: [
    { label: "Dashboard Home", to: "/customer", icon: "bi-house-door" },
    { label: "New Request", to: "/customer/wizard", icon: "bi-plus-circle" },
    { label: "My Cards", to: "/customer/cards", icon: "bi-credit-card" },
    { label: "Settlements", to: "/customer/statements", icon: "bi-file-earmark-text" },
    { label: "Transactions", to: "/customer/transactions", icon: "bi-arrow-left-right" },
    { label: "Payments", to: "/customer/payments", icon: "bi-wallet2" }
  ],
  underwriter: [
    { label: "Dashboard Home", to: "/underwriter" },
    { label: "Applications", to: "/underwriter/applications" },
    { label: "Credit Scores", to: "/underwriter/credit-scores" },
    { label: "Decision Page", to: "/underwriter/decisions" }
  ],
  operations: [
    { label: "Dashboard Home", to: "/operations" },
    { label: "Card Issuance", to: "/operations/card-issuance" },
    { label: "Card Accounts", to: "/operations/card-accounts" },
    { label: "Transactions", to: "/operations/transactions" },
    { label: "Statements", to: "/operations/statements" },
    { label: "Payments", to: "/operations/payments" }
  ],
  admin: [
    { label: "Dashboard Home", to: "/admin" },
    { label: "Fee Config", to: "/admin/fees" },
    { label: "Audit Log", to: "/admin/audit-logs" }
  ]
};

function Layout({ section, title, children }) {
  const links = navSections[section] || [];
  const session = getStoredSession();
  const navigate = useNavigate();

  return (
    <div className="app-shell">
      <aside className="sidebar-panel">
        <div>
          <Link to="/" className="brand-link text-decoration-none">
            <div className="brand-badge">CM</div>
            <div>
              <h4 className="mb-0 text-white">CardMaster</h4>
              <small className="text-white-50">Credit Card Control Center</small>
            </div>
          </Link>
        </div>

        <div className="mt-4">
          <p className="sidebar-label">{title}</p>
          {!session && (
            <div className="session-panel mb-3">
              <Link to="/login" className="btn btn-sm btn-light w-100 mb-2">
                Login
              </Link>
              <Link to="/register" className="btn btn-sm btn-outline-light w-100">
                Register
              </Link>
            </div>
          )}
          <nav className="nav flex-column gap-2">
            {links.map((link) => (
              <NavLink
                key={link.to}
                to={link.to}
                className={({ isActive }) =>
                  `nav-link sidebar-link ${isActive ? "active" : ""}`
                }
              >
                {link.label}
              </NavLink>
            ))}
          </nav>
        </div>
      </aside>

      <main className="content-panel">
        <div className="hero-banner mb-4 d-flex justify-content-between align-items-center">
          <div>
            <h1 className="h3 mb-0">{title}</h1>
          </div>
          {session && (
            <div className="d-flex align-items-center gap-3">
              <div className="text-end d-none d-md-block">
                <div className="small text-muted fw-semibold">{session.name || "User"}</div>
                <div className="small text-muted" style={{ fontSize: '0.75rem' }}>{session.email}</div>
              </div>
              <button
                className="btn btn-sm btn-outline-danger"
                onClick={async () => {
                  const logoutName = session?.name || session?.email || "User";
                  try {
                    await authApi.logout();
                    saveLogoutMessage(`Goodbye, ${logoutName}. You have been logged out successfully.`);
                  } catch (error) {
                    clearSession();
                    saveLogoutMessage(`Goodbye, ${logoutName}. Your session has been closed.`);
                  }
                  navigate("/logout");
                }}
              >
                <i className="bi bi-box-arrow-right me-1"></i> Logout
              </button>
            </div>
          )}
        </div>
        {children}
      </main>
    </div>
  );
}

export default Layout;
