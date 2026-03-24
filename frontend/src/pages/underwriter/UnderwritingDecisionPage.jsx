import { useState } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { applications, underwritingHistory } from "../../data/mockData";
import { underwriterApi } from "../../services/api";

const initialForm = {
  applicationId: String(applications[0]?.applicationId || ""),
  decision: "APPROVED",
  approvedLimit: "",
  notes: ""
};

function UnderwritingDecisionPage() {
  const [formData, setFormData] = useState(initialForm);
  const [history, setHistory] = useState(underwritingHistory);
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
    const selectedApplication = applications.find(
      (application) => String(application.applicationId) === formData.applicationId
    );

    const payload = {
      decision: formData.decision,
      approvedLimit: Number(formData.approvedLimit),
      remarks: formData.notes
    };

    try {
      const response = await underwriterApi.createDecision(formData.applicationId, payload);
      const newDecision = {
        decisionId: Date.now(),
        applicationId: response?.applicationId || Number(formData.applicationId),
        customerEmail: selectedApplication?.customerEmail || "",
        underwriter: "Current Underwriter",
        decision: response?.decision || formData.decision,
        approvedLimit: response?.approvedLimit || Number(formData.approvedLimit),
        remarks: response?.remarks || formData.notes,
        decisionDate: response?.decisionDate || new Date().toISOString().slice(0, 10)
      };

      setHistory((current) => [newDecision, ...current]);
      setMessage("Underwriting decision saved through the backend.");
      setFormData(initialForm);
    } catch (submitError) {
      setError(submitError.message || "Unable to save the decision.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Layout section="underwriter" title="Underwriter Dashboard">
      <PageHeader
        title="Underwriting Decision Page"
        subtitle="Approve, reject or place applications on conditional approval with notes."
      />

      <div className="row g-4">
        <div className="col-lg-5">
          <div className="card border-0 shadow-sm">
            <div className="card-body p-4">
              <form onSubmit={handleSubmit} className="row g-3">
                <div className="col-12">
                  <label className="form-label">Application</label>
                  <select className="form-select" name="applicationId" value={formData.applicationId} onChange={handleChange} required>
                    {applications.map((application) => (
                      <option key={application.applicationId} value={application.applicationId}>
                        {application.customerEmail} - {application.productName}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="col-12">
                  <label className="form-label">Decision</label>
                  <select className="form-select" name="decision" value={formData.decision} onChange={handleChange}>
                    <option>APPROVED</option>
                    <option>REJECTED</option>
                    <option>CONDITIONAL</option>
                  </select>
                </div>
                <div className="col-12">
                  <label className="form-label">Approved Limit</label>
                  <input type="number" className="form-control" name="approvedLimit" value={formData.approvedLimit} onChange={handleChange} required />
                </div>
                <div className="col-12">
                  <label className="form-label d-flex justify-content-between">
                    <span>Underwriter Notes</span>
                    <span className={`small ${formData.notes.length > 200 ? 'text-danger' : 'text-muted'}`}>
                      {formData.notes.length}/200
                    </span>
                  </label>
                  <textarea 
                    className="form-control" 
                    rows="4" 
                    name="notes" 
                    value={formData.notes} 
                    onChange={handleChange} 
                    required 
                    maxLength="200"
                  />
                </div>
                <div className="col-12">
                  <button className="btn btn-primary" disabled={loading}>
                    {loading ? "Saving..." : "Save Decision"}
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
              <h5 className="mb-3">Underwriting History</h5>
              <DataTable
                columns={[
                  { key: "customerEmail", label: "Customer Email" },
                  { key: "underwriter", label: "Underwriter" },
                  { key: "decision", label: "Decision", type: "status" },
                  { key: "approvedLimit", label: "Approved Limit" },
                  { key: "remarks", label: "Remarks" }
                ]}
                rows={history}
              />
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
}

export default UnderwritingDecisionPage;
