import { Link } from "react-router-dom";
import { consumeLogoutMessage } from "../../utils/auth";

function LogoutPage() {
  const message = consumeLogoutMessage() || "You have been logged out successfully.";

  return (
    <div className="auth-shell">
      <div className="auth-aurora auth-aurora-left"></div>
      <div className="auth-aurora auth-aurora-right"></div>
      <div className="container py-4">
        <div className="row justify-content-center align-items-center min-vh-100">
          <div className="col-lg-5">
            <div className="auth-card shadow-lg text-center">
              <div className="auth-card-top justify-content-center">
                <div className="brand-badge">CM</div>
              </div>
              <h2 className="h3 fw-bold mb-3">Logout</h2>
              <div className="alert alert-success">{message}</div>
              <p className="text-muted mb-4">You can login again whenever you are ready.</p>
              <Link to="/login" className="btn btn-primary auth-submit-btn">
                Back To Login
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default LogoutPage;
