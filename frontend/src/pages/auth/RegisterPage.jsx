import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { authApi } from "../../services/api";

const initialForm = {
  name: "",
  email: "",
  phone: "",
  password: "",
  role: "CUSTOMER"
};

function RegisterPage() {
  const [formData, setFormData] = useState(initialForm);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  function handleChange(event) {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setLoading(true);
    setError("");
    setMessage("");

    try {
      await authApi.register(formData);
      setMessage("Registration successful. Continue to login with this email and role.");
      setFormData(initialForm);
      setTimeout(() => navigate("/login", { replace: true }), 1200);
    } catch (submitError) {
      setError(submitError.message || "Registration failed.");
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
          <div className="col-lg-5">
            <div className="auth-showcase">
              <p className="auth-kicker">CardMaster Access</p>
              <h1 className="auth-title">Create your CardMaster account.</h1>
              <p className="auth-copy">
                Set up a user account with the correct role and continue to sign in.
              </p>

              <div className="auth-role-grid">
                <div className="auth-role-card">
                  <strong>Customer Access</strong>
                  <span>Application and service views</span>
                </div>
                <div className="auth-role-card">
                  <strong>Underwriter Access</strong>
                  <span>Assessment and decision workflows</span>
                </div>
                <div className="auth-role-card">
                  <strong>Operations Access</strong>
                  <span>Card, statement, and payment actions</span>
                </div>
                <div className="auth-role-card">
                  <strong>Admin Access</strong>
                  <span>User and product management</span>
                </div>
              </div>
            </div>
          </div>

          <div className="col-lg-6 ms-auto">
            <div className="auth-card shadow-lg">
              <div className="auth-card-top">
                <div className="brand-badge">CM</div>
                <div>
                  <p className="auth-kicker mb-1">Create User</p>
                  <h2 className="h3 fw-bold mb-0">Registration</h2>
                </div>
              </div>

              <p className="text-muted mb-4">
                Fill in the account details below to create a new user.
              </p>

              <form onSubmit={handleSubmit} className="row g-3">
                <div className="col-md-6">
                  <label className="form-label">Name</label>
                  <input className="form-control auth-input" name="name" value={formData.name} onChange={handleChange} required />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Role</label>
                  <select className="form-select auth-input" name="role" value={formData.role} onChange={handleChange}>
                    <option>CUSTOMER</option>
                    <option>OFFICER</option>
                    <option>UNDERWRITER</option>
                    <option>RISK</option>
                    <option>ADMIN</option>
                  </select>
                </div>
                <div className="col-md-6">
                  <label className="form-label">Email</label>
                  <input type="email" className="form-control auth-input" name="email" value={formData.email} onChange={handleChange} required />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Phone</label>
                  <input className="form-control auth-input" name="phone" value={formData.phone} onChange={handleChange} required />
                </div>
                <div className="col-12">
                  <label className="form-label">Password</label>
                  <input type="password" className="form-control auth-input" name="password" value={formData.password} onChange={handleChange} minLength="8" required />
                </div>
                <div className="col-12 d-grid">
                  <button className="btn btn-primary auth-submit-btn" disabled={loading}>
                    {loading ? "Registering..." : "Create Account"}
                  </button>
                </div>
              </form>

              {message ? <div className="alert alert-success mt-4 mb-0">{message}</div> : null}
              {error ? <div className="alert alert-danger mt-4 mb-0">{error}</div> : null}

              <p className="mt-4 mb-0 text-muted">
                Already have an account? <Link to="/login">Go to login</Link>.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default RegisterPage;
