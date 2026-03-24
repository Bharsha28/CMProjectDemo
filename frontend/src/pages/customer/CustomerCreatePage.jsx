import { useEffect, useState } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import { customerApi } from "../../services/api";
import { getStoredSession } from "../../utils/auth";

const initialForm = {
  name: "",
  dob: "",
  address: "",
  email: "",
  phone: "",
  income: "",
  employmentType: "Salaried",
  status: "Active"
};

function CustomerCreatePage() {
  const [formData, setFormData] = useState(initialForm);
  const [submittedData, setSubmittedData] = useState(null);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const sessionEmail = getStoredSession()?.email || "";
    if (sessionEmail) {
      setFormData((current) => ({
        ...current,
        email: sessionEmail
      }));
    }
  }, []);

  function handleChange(event) {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setLoading(true);
    setError("");
    setMessage("");

    const payload = {
      name: formData.name,
      dob: formData.dob,
      contactInfo: {
        address: formData.address,
        email: formData.email,
        phone: formData.phone
      },
      income: Number(formData.income),
      employmentType: formData.employmentType,
      status: formData.status
    };

    try {
      const response = await customerApi.createCustomer(payload);
      const createdCustomer = response?.data || payload;
      setSubmittedData(createdCustomer);
      setMessage("Customer saved through the backend successfully.");
      setFormData((current) => ({
        ...initialForm,
        email: current.email
      }));
    } catch (submitError) {
      setError(submitError.message || "Customer creation failed.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Layout section="customer" title="Customer Dashboard">
      <PageHeader
        title="Customer Create Page"
        subtitle="Create a customer profile before submitting a card application."
      />

      <div className="row g-4">
        <div className="col-lg-8">
          <div className="card border-0 shadow-sm">
            <div className="card-body p-4">
              <form onSubmit={handleSubmit} className="row g-3">
                <div className="col-md-6">
                  <label className="form-label">Customer Name</label>
                  <input className="form-control" name="name" value={formData.name} onChange={handleChange} required />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Date of Birth</label>
                  <input type="date" className="form-control" name="dob" value={formData.dob} onChange={handleChange} required />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Address</label>
                  <input className="form-control" name="address" value={formData.address} onChange={handleChange} required />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Email</label>
                  <input type="email" className="form-control" name="email" value={formData.email} onChange={handleChange} required readOnly disabled />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Phone</label>
                  <input className="form-control" name="phone" value={formData.phone} onChange={handleChange} required />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Annual Income</label>
                  <input type="number" className="form-control" name="income" value={formData.income} onChange={handleChange} required />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Employment Type</label>
                  <select className="form-select" name="employmentType" value={formData.employmentType} onChange={handleChange}>
                    <option>Salaried</option>
                    <option>SelfEmployed</option>
                    <option>Student</option>
                    <option>Retired</option>
                    <option>Unemployed</option>
                  </select>
                </div>
                <div className="col-md-6">
                  <label className="form-label">Status</label>
                  <select className="form-select" name="status" value={formData.status} onChange={handleChange}>
                    <option>Active</option>
                    <option>Inactive</option>
                    <option>Suspended</option>
                    <option>Closed</option>
                  </select>
                </div>
                <div className="col-12">
                  <button className="btn btn-primary" disabled={loading}>
                    {loading ? "Saving..." : "Create Customer"}
                  </button>
                </div>
              </form>
              {message ? <div className="alert alert-success mt-4 mb-0">{message}</div> : null}
              {error ? <div className="alert alert-danger mt-4 mb-0">{error}</div> : null}
            </div>
          </div>
        </div>

        <div className="col-lg-4">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-body p-4">
              <h5 className="mb-3">Preview</h5>
              {submittedData ? (
                <div className="small-details">
                  <p><strong>Name:</strong> {submittedData.name}</p>
                  <p><strong>Address:</strong> {submittedData.address || submittedData.contactInfo?.address}</p>
                  <p><strong>Email:</strong> {submittedData.email || submittedData.contactInfo?.email}</p>
                  <p><strong>Income:</strong> {submittedData.income}</p>
                  <p className="mb-0"><strong>Status:</strong> {submittedData.status}</p>
                </div>
              ) : (
                <p className="text-muted mb-0">Submit the form to see the created customer preview.</p>
              )}
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
}

export default CustomerCreatePage;
