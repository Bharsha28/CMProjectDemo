import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { authApi } from "../../services/api";
import { getDefaultDashboardPath, saveSession } from "../../utils/auth";

const initialForm = {
  email: "",
  password: ""
};

function LoginPage() {
  const [formData, setFormData] = useState(initialForm);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [emailError, setEmailError] = useState("");
  const navigate = useNavigate();

  const validateEmail = (email) => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  };

  function handleChange(event) {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));

    if (name === "email") {
      if (value && !validateEmail(value)) {
        setEmailError("Invalid email format");
      } else {
        setEmailError("");
      }
    }
  }

  async function handleSubmit(event) {
    event.preventDefault();
    if (emailError) return;

    setLoading(true);
    setError("");
    setMessage("");

    try {
      const response = await authApi.login(formData);
      saveSession({
        token: response.token,
        email: formData.email,
        role: response.role || "",
        name: response.name || formData.email
      });
      setMessage(
        response.isMockLogin
          ? "Mock login successful. You can now explore this role dashboard without the backend."
          : "Login successful. You can now use the protected dashboard actions."
      );
      navigate(getDefaultDashboardPath(response.role), { replace: true });
    } catch (submitError) {
      const msg = submitError.message || "";
      setError(msg.includes("SQL") || msg.length > 60 ? "Invalid email or password." : msg || "Login failed.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-shell">
      <div className="auth-aurora auth-aurora-left"></div>
      <div className="auth-aurora auth-aurora-right"></div>
      <div className="container py-4">
        <div className="row align-items-center g-4 min-vh-100">
          <div className="col-lg-6">
            <div className="auth-showcase">
              <p className="auth-kicker">CardMaster Access</p>
              <h1 className="auth-title">Welcome to CardMaster.</h1>
              <p className="auth-copy">
                Sign in to continue to your workspace.
              </p>

              <div className="auth-chip-row">
                <span className="auth-chip">Secure Access</span>
                <span className="auth-chip">Role Based</span>
                <span className="auth-chip">Banking Portal</span>
              </div>

              <div className="auth-feature-list">
                <div className="auth-feature-card">
                  <i className="bi bi-shield-check"></i>
                  <div>
                    <h5>Protected sign in</h5>
                    <p>Your session opens only the workspace assigned to your account.</p>
                  </div>
                </div>
                <div className="auth-feature-card">
                  <i className="bi bi-credit-card-2-front"></i>
                  <div>
                    <h5>Unified card operations</h5>
                    <p>Manage applications, cards, payments, and servicing from one portal.</p>
                  </div>
                </div>
                <div className="auth-feature-card">
                  <i className="bi bi-building-lock"></i>
                  <div>
                    <h5>Institution-ready access</h5>
                    <p>Designed for customer, underwriting, operations, and admin teams.</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="col-lg-5 ms-auto">
            <div className="auth-card shadow-lg">
              <div className="auth-card-top">
                <div className="brand-badge">CM</div>
                <div>
                  <p className="auth-kicker mb-1">Welcome</p>
                  <h2 className="h3 fw-bold mb-0">Login</h2>
                </div>
              </div>

              <p className="text-muted mb-4">
                Enter your email and password to access CardMaster.
              </p>

              <form onSubmit={handleSubmit} className="row g-3">
                <div className="col-12">
                  <label className="form-label">Email</label>
                  <input
                    type="email"
                    className={`form-control auth-input ${emailError ? 'is-invalid' : ''}`}
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    placeholder="name@bank.com"
                    required
                  />
                  {emailError && <div className="invalid-feedback">{emailError}</div>}
                </div>
                <div className="col-12">
                  <label className="form-label">Password</label>
                  <div className="input-group">
                    <input
                      type={showPassword ? "text" : "password"}
                      className="form-control auth-input"
                      name="password"
                      value={formData.password}
                      onChange={handleChange}
                      placeholder="Enter your password"
                      required
                    />
                    <button
                      className="btn btn-outline-secondary auth-input-icon"
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                    >
                      <i className={`bi bi-eye${showPassword ? '-slash' : ''}`}></i>
                    </button>
                  </div>
                </div>
                <div className="auth-status-container" style={{ minHeight: "60px" }}>
                  {message ? <div className="alert alert-success mt-4 mb-0 text-truncate" title={message}>{message}</div> : null}
                  {error ? <div className="alert alert-danger mt-4 mb-0 text-truncate" title={error}>{error}</div> : null}
                </div>

                <div className="col-12 d-flex justify-content-end">
                  <button className="btn btn-primary auth-submit-btn-minimized" disabled={loading}>
                    {loading ? "Logging in..." : "Login"}
                  </button>
                </div>
              </form>

              <p className="mt-2 mb-0 text-muted">
                Need an account? <Link to="/register">Create one here</Link>.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;

