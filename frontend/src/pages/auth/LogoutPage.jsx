import { useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { consumeLogoutMessage } from "../../utils/auth";

function LogoutPage() {
  const navigate = useNavigate();
  const message = consumeLogoutMessage() || "You have been logged out successfully.";

  useEffect(() => {
    const timer = setTimeout(() => {
      navigate("/login", { replace: true });
    }, 3000);
    return () => clearTimeout(timer);
  }, [navigate]);

  return (
    <div className="auth-shell">
      <div className="auth-aurora auth-aurora-left"></div>
      <div className="auth-aurora auth-aurora-right"></div>
      <div className="container py-4">
        <div className="row align-items-center g-4 min-vh-100">
          <div className="col-lg-6">
            <div className="auth-showcase">
              <p className="auth-kicker">Session Ended</p>
              <h1 className="auth-title">Goodbye for now.</h1>
              <p className="auth-copy">
                Thank you for using CardMaster. Your session has been securely closed.
              </p>

              <div className="auth-chip-row">
                <span className="auth-chip">Secured</span>
                <span className="auth-chip">Logged Out</span>
              </div>

              <div className="auth-feature-list mt-4">
                <div className="auth-feature-card">
                  <i className="bi bi-shield-check"></i>
                  <div>
                    <h5>Safe and secure</h5>
                    <p>Your session cache and data have been cleared from this device.</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="col-lg-5 ms-auto">
            <div className="auth-card shadow-lg text-center p-5">
              <div className="auth-card-top justify-content-center mb-4">
                <div className="brand-badge" style={{ width: '64px', height: '64px', fontSize: '1.5rem' }}>CM</div>
              </div>
              <h2 className="h3 fw-bold mb-3">Logout</h2>
              <div className="alert alert-success mb-4 text-start">{message}</div>
              <p className="text-muted mb-4">You will be redirected to the login page shortly, or you can return immediately.</p>
              <div className="d-flex justify-content-end">
                <Link to="/login" className="btn btn-primary auth-submit-btn-minimized">
                  Back To Login
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default LogoutPage;
