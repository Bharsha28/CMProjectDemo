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
  const [showPassword, setShowPassword] = useState(false);
  const [emailError, setEmailError] = useState("");
  const [passwordError, setPasswordError] = useState("");
  const [phoneError, setPhoneError] = useState("");
  const navigate = useNavigate();

  const validateEmail = (email) => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  };

  function handleChange(event) {
    const { name, value } = event.target;

    if (name === "phone") {
      const cleaned = value.replace(/\D/g, "");
      if (cleaned.length > 10) return;
      setFormData((current) => ({ ...current, [name]: cleaned }));
      if (cleaned.length > 0 && cleaned.length !== 10) {
        setPhoneError("Phone must be exactly 10 digits");
      } else {
        setPhoneError("");
      }
      return;
    }

    setFormData((current) => ({ ...current, [name]: value }));

    if (name === "email") {
      if (value && !validateEmail(value)) {
        setEmailError("Invalid email format");
      } else {
        setEmailError("");
      }
    }

    if (name === "password") {
      if (value && value.length < 8) {
        setPasswordError("Password must be at least 8 characters");
      } else {
        setPasswordError("");
      }
    }
  }

  async function handleSubmit(event) {
    event.preventDefault();
    if (emailError || passwordError || phoneError || formData.phone.length !== 10) {
      if (formData.phone.length !== 10) setPhoneError("Phone must be exactly 10 digits");
      return;
    }

    setLoading(true);
    setError("");
    setMessage("");

    try {
      await authApi.register(formData);
      setMessage("Registration successful. Continue to login with this email and role.");
      setFormData(initialForm);
      setTimeout(() => navigate("/login", { replace: true }), 1200);
    } catch (submitError) {
      const msg = (submitError.message || "").toLowerCase();
      let parsedError = "Registration failed. Please check your details.";
      
      if (msg.includes("duplicate")) {
        if (msg.includes("phone")) {
          parsedError = "This phone number is already registered.";
        } else if (msg.includes("email")) {
          parsedError = "This email address is already registered.";
        } else {
          parsedError = "Registration failed. User may already exist.";
        }
      } else if (msg.length < 60 && !msg.includes("sql")) {
        parsedError = submitError.message;
      }
      
      setError(parsedError);
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
                    <option>ADMIN</option>
                  </select>
                </div>
                <div className="col-md-6">
                  <label className="form-label">Email</label>
                  <input 
                    type="email" 
                    className={`form-control auth-input ${emailError ? 'is-invalid' : ''}`} 
                    name="email" 
                    value={formData.email} 
                    onChange={handleChange} 
                    required 
                  />
                  {emailError && <div className="invalid-feedback">{emailError}</div>}
                </div>
                <div className="col-md-6">
                  <label className="form-label">Phone</label>
                  <input 
                    className={`form-control auth-input ${phoneError ? 'is-invalid' : ''}`} 
                    name="phone" 
                    value={formData.phone} 
                    onChange={handleChange} 
                    placeholder="10 digits only"
                    required 
                  />
                  {phoneError && <div className="invalid-feedback">{phoneError}</div>}
                </div>
                <div className="col-12">
                  <label className="form-label">Password</label>
                  <div className="input-group">
                    <input 
                      type={showPassword ? "text" : "password"} 
                      className={`form-control auth-input ${passwordError ? 'is-invalid' : ''}`} 
                      name="password" 
                      value={formData.password} 
                      onChange={handleChange} 
                      placeholder="Min 8 characters"
                      required 
                    />
                    <button
                      className="btn btn-outline-secondary auth-input-icon"
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                    >
                      <i className={`bi bi-eye${showPassword ? '-slash' : ''}`}></i>
                    </button>
                    {passwordError && <div className="invalid-feedback w-100">{passwordError}</div>}
                  </div>
                </div>
                <div className="auth-status-container" style={{ minHeight: "60px", display: "flex", alignItems: "center" }}>
                  {message ? <div className="alert alert-success m-0 w-100 text-truncate" title={message}>{message}</div> : null}
                  {error ? <div className="alert alert-danger m-0 w-100 text-truncate" title={error}>{error}</div> : null}
                </div>

                <div className="col-12 d-flex justify-content-end">
                  <button className="btn btn-primary auth-submit-btn-minimized" disabled={loading}>
                    {loading ? "Registering..." : "Create"}
                  </button>
                </div>
              </form>

              <p className="mt-1 mb-0 text-muted">
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

