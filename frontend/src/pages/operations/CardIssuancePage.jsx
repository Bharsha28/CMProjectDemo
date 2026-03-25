import { useState, useEffect } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { operationsApi, underwriterApi } from "../../services/api";

const initialForm = {
  applicationId: "",
  maskedCardNumber: "",
  expiryDate: "",
  cvvHash: "",
  status: "ISSUED"
};

function CardIssuancePage() {
  const [formData, setFormData] = useState(initialForm);
  const [cardRows, setCardRows] = useState([]);
  const [applicationList, setApplicationList] = useState([]);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    async function load() {
      try {
        const [cData, aData] = await Promise.all([
          operationsApi.getCards().catch(() => []),
          underwriterApi.getApplications().catch(() => [])
        ]);
        setCardRows(cData);
        setApplicationList(aData);
        const approved = aData.find(a => a.status === "Approved");
        if (approved) {
           setFormData(f => ({...f, applicationId: String(approved.applicationId)}));
        }
      } catch (e) { console.error(e); }
    }
    load();
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
    const selectedApplication = applicationList.find(
      (application) => String(application.applicationId) === formData.applicationId
    );

    const payload = {
      applicationId: Number(formData.applicationId),
      maskedCardNumber: formData.maskedCardNumber,
      expiryDate: `${formData.expiryDate}-01`,
      cvvHash: formData.cvvHash,
      status: formData.status
    };

    try {
      const response = await operationsApi.createCard(payload);
    const newCard = {
      cardId: response?.cardId || Date.now(),
      applicationId: response?.applicationId || Number(formData.applicationId),
      customerEmail: selectedApplication?.customerEmail || "",
      customerName: selectedApplication?.customerName || "",
      productName: selectedApplication?.productName || "Card Product",
      maskedCardNumber: response?.maskedCardNumber || formData.maskedCardNumber,
      expiryDate: response?.expiryDate || formData.expiryDate,
      cvvHash: response?.cvvHash || formData.cvvHash,
      status: response?.status || formData.status
    };

    setCardRows((current) => [newCard, ...current]);
      setMessage("Card issuance saved through the backend.");
    setFormData(initialForm);
    } catch (submitError) {
      setError(submitError.message || "Unable to issue the card.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Layout section="operations" title="Operations Dashboard">
      <PageHeader
        title="Card Issuance Page"
        subtitle="Generate a new card, activate it and update card status."
      />

      <div className="card border-0 shadow-sm mb-4">
        <div className="card-body p-4">
          <form onSubmit={handleSubmit} className="row g-3">
            <div className="col-md-6">
              <label className="form-label">Approved Application</label>
              <select className="form-select" name="applicationId" value={formData.applicationId} onChange={handleChange} required>
                <option value="">Select Approved Application</option>
                {applicationList
                  .filter((application) => ["Approved", "Conditional"].includes(application.status))
                  .map((application) => (
                    <option key={application.applicationId} value={application.applicationId}>
                      {application.customerName} - {application.productName} (ID: {application.applicationId})
                    </option>
                  ))}
              </select>
            </div>
            <div className="col-md-6">
              <label className="form-label">Masked Card Number</label>
              <input className="form-control" name="maskedCardNumber" value={formData.maskedCardNumber} onChange={handleChange} placeholder="4567 XXXX XXXX 1234" required />
            </div>
            <div className="col-md-6">
              <label className="form-label">Expiry Date</label>
              <input type="month" className="form-control" name="expiryDate" value={formData.expiryDate} onChange={handleChange} required />
            </div>
            <div className="col-md-6">
              <label className="form-label">CVV Hash</label>
              <input className="form-control" name="cvvHash" value={formData.cvvHash} onChange={handleChange} placeholder="hashed-cvv-value" required />
            </div>
            <div className="col-md-6">
              <label className="form-label">Card Status</label>
              <select className="form-select" name="status" value={formData.status} onChange={handleChange}>
                <option>ISSUED</option>
                <option>ACTIVE</option>
                <option>BLOCKED</option>
              </select>
            </div>
            <div className="col-12 d-flex justify-content-end mt-3">
              <button className="btn btn-primary px-4" disabled={loading}>
                {loading ? "Saving..." : "Generate"}
              </button>
            </div>
          </form>
          {message ? <div className="alert alert-success mt-4 mb-0">{message}</div> : null}
          {error ? <div className="alert alert-danger mt-4 mb-0">{error}</div> : null}
        </div>
      </div>

      <div className="card border-0 shadow-sm">
        <div className="card-body p-0">
          <DataTable
            columns={[
              { key: "customerName", label: "Customer" },
              { key: "cardProductName", label: "Product", render: (row) => row.cardProductName || row.productName || "-" },
              { key: "expiryDate", label: "Expiry" },
              { key: "status", label: "Status", type: "status" }
            ]}
            rows={cardRows}
          />
        </div>
      </div>
    </Layout>
  );
}

export default CardIssuancePage;
