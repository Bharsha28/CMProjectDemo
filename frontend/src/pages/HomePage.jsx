import { Link } from "react-router-dom";
import { clearSession, getStoredSession } from "../utils/auth";

const dashboardCards = [
  {
    title: "Customer Dashboard",
    description: "Access your cards, view transaction history, and manage your financial profile in one place.",
    to: "/customer",
    icon: "bi-person-circle",
    color: "primary"
  },
  {
    title: "Underwriter Dashboard",
    description: "Evaluate credit applications, analyze risk factors, and record underwriting decisions.",
    to: "/underwriter",
    icon: "bi-clipboard-data",
    color: "warning"
  },
  {
    title: "Operations Dashboard",
    description: "Streamline card issuance, manage accounts, and monitor system-wide payment activities.",
    to: "/operations",
    icon: "bi-gear-wide-connected",
    color: "success"
  },
  {
    title: "Admin Dashboard",
    description: "Oversee system settings, configure fee structures, and audit all platform activities.",
    to: "/admin",
    icon: "bi-shield-lock",
    color: "info"
  }
];

function HomePage() {
  const session = getStoredSession();

  return (
    <div className="landing-page">
      <div className="landing-hero shadow-sm">
        <div className="row align-items-center">
          <div className="col-lg-7">
            <p className="text-uppercase fw-semibold small mb-2 text-primary">
              Welcome to CardMaster
            </p>
            <h1 className="display-5 fw-bold mb-3">
              Modern Credit Card Issuance & Operations Platform
            </h1>
            <p className="lead text-muted mb-4">
              A comprehensive solution for managing the entire credit card lifecycle—from seamless onboarding to sophisticated risk management.
            </p>
            <div className="d-flex flex-wrap gap-2">
              {session ? (
                <>
                  <Link to="/customer" className="btn btn-primary btn-lg">
                    Go to Dashboard
                  </Link>
                  <button
                    className="btn btn-outline-secondary btn-lg"
                    onClick={() => {
                      clearSession();
                      window.location.reload();
                    }}
                  >
                    Logout
                  </button>
                </>
              ) : (
                <>
                  <Link to="/login" className="btn btn-primary btn-lg">
                    Login
                  </Link>
                  <Link to="/register" className="btn btn-outline-dark btn-lg">
                    Sign Up
                  </Link>
                </>
              )}
            </div>
            {session ? (
              <div className="alert alert-light border mt-4 mb-0">
                Logged in as <strong>{session.email}</strong>
              </div>
            ) : null}
          </div>
          <div className="col-lg-5">
            <div className="row g-3 mt-3 mt-lg-0">
              {["Smart Profiles", "Risk Analysis", "Live Ops", "Analytics"].map((item) => (
                <div className="col-6" key={item}>
                  <div className="mini-tile">
                    <span>{item}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      <div className="row g-4 mt-1">
        {dashboardCards.map((card) => (
          <div className="col-lg-6" key={card.title}>
            <div className="card border-0 shadow-sm h-100">
              <div className="card-body p-4">
                <div className={`icon-chip bg-${card.color}-subtle text-${card.color}`}>
                  <i className={`bi ${card.icon}`}></i>
                </div>
                <h3 className="h4 mt-3">{card.title}</h3>
                <p className="text-muted">{card.description}</p>
                <Link to={card.to} className="btn btn-outline-primary">
                  Enter Dashboard
                </Link>
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="mt-5 p-5 rounded-4 bg-white shadow-sm border">
        <div className="row align-items-center">
          <div className="col-md-7">
            <h2 className="fw-bold mb-3">About CardMaster</h2>
            <p className="text-muted mb-4">
              CardMaster is a full-stack credit card management system designed to streamline the entire lifecycle of a credit card—from initial application and underwriting to card issuance, transaction monitoring, and financial settlement.
            </p>
            <ul className="list-unstyled mb-0">
              <li className="mb-2 d-flex align-items-start gap-2">
                <i className="bi bi-check-circle-fill text-success mt-1"></i>
                <div><strong>Seamless Onboarding:</strong> Integrated customer profiling and application wizard for a friction-less experience.</div>
              </li>
              <li className="mb-2 d-flex align-items-start gap-2">
                <i className="bi bi-check-circle-fill text-success mt-1"></i>
                <div><strong>Intelligent Underwriting:</strong> Data-driven decision making with credit score analysis and conditional approvals.</div>
              </li>
              <li className="mb-2 d-flex align-items-start gap-2">
                <i className="bi bi-check-circle-fill text-success mt-1"></i>
                <div><strong>Real-time Operations:</strong> Efficient card issuance, account management, and transaction tracking.</div>
              </li>
              <li className="mb-2 d-flex align-items-start gap-2">
                <i className="bi bi-check-circle-fill text-success mt-1"></i>
                <div><strong>Robust Administration:</strong> Full control over products, fees, and system audit logs for maximum transparency.</div>
              </li>
            </ul>
          </div>
          <div className="col-md-5 text-center d-none d-md-block">
            <div className="p-4 bg-primary-subtle rounded-circle d-inline-block">
              <i className="bi bi-shield-check display-1 text-primary"></i>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default HomePage;
