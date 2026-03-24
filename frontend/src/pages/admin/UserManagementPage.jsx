import { useState } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { users } from "../../data/mockData";
import { adminApi } from "../../services/api";

const initialForm = {
  name: "",
  email: "",
  phone: "",
  password: "",
  role: "CUSTOMER"
};

function UserManagementPage() {
  const [formData, setFormData] = useState(initialForm);
  const [rows, setRows] = useState(users);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

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
      const response = await adminApi.registerUser(formData);
      const createdUser = response?.data || formData;
      setRows((current) => [createdUser, ...current]);
      setMessage("User saved through the backend.");
      setFormData(initialForm);
    } catch (submitError) {
      setError(submitError.message || "Unable to create the user.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Layout section="admin" title="Admin Dashboard">
      <PageHeader
        title="User Management Page"
        subtitle="Create users and assign roles for customer, underwriter, operations or admin."
      />

      <div className="row g-4">
        <div className="col-lg-5">
          <div className="card border-0 shadow-sm">
            <div className="card-body p-4">
              <form onSubmit={handleSubmit} className="row g-3">
                <div className="col-12">
                  <label className="form-label">Name</label>
                  <input className="form-control" name="name" value={formData.name} onChange={handleChange} required />
                </div>
                <div className="col-12">
                  <label className="form-label">Email</label>
                  <input type="email" className="form-control" name="email" value={formData.email} onChange={handleChange} required />
                </div>
                <div className="col-12">
                  <label className="form-label">Phone</label>
                  <input className="form-control" name="phone" value={formData.phone} onChange={handleChange} required />
                </div>
                <div className="col-12">
                  <label className="form-label">Password</label>
                  <input type="password" className="form-control" name="password" value={formData.password} onChange={handleChange} minLength="8" required />
                </div>
                <div className="col-12">
                  <label className="form-label">Role</label>
                  <select className="form-select" name="role" value={formData.role} onChange={handleChange}>
                    <option>CUSTOMER</option>
                    <option>UNDERWRITER</option>
                    <option>OFFICER</option>
                    <option>RISK</option>
                    <option>ADMIN</option>
                  </select>
                </div>
                <div className="col-12">
                  <button className="btn btn-primary" disabled={loading}>
                    {loading ? "Saving..." : "Create User"}
                  </button>
                </div>
              </form>
              {message ? <div className="alert alert-success mt-4 mb-0">{message}</div> : null}
              {error ? <div className="alert alert-danger mt-4 mb-0">{error}</div> : null}
            </div>
          </div>
        </div>

        <div className="col-lg-7">
          <div className="card border-0 shadow-sm">
            <div className="card-body">
              <DataTable
                columns={[
                  { key: "name", label: "Name" },
                  { key: "email", label: "Email" },
                  { key: "phone", label: "Phone" },
                  { key: "role", label: "Role" }
                ]}
                rows={rows}
              />
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
}

export default UserManagementPage;
