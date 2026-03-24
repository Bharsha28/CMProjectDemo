import { useEffect, useState } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { accounts, statements } from "../../data/mockData";
import { operationsApi } from "../../services/api";

const initialForm = {
  accountId: String(accounts[0]?.accountId || ""),
  periodStart: "",
  periodEnd: ""
};

function StatementsListPage() {
  const [rows, setRows] = useState(statements);
  const [formData, setFormData] = useState(initialForm);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    async function loadStatements() {
      const response = await operationsApi.getStatements();
      setRows(Array.isArray(response) ? response : statements);
    }

    loadStatements();
  }, []);

  function handleChange(event) {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));
  }

  async function handleGenerate(event) {
    event.preventDefault();
    setLoading(true);
    setError("");
    setMessage("");

    try {
      const created = await operationsApi.createStatement({
        accountId: Number(formData.accountId),
        periodStart: formData.periodStart,
        periodEnd: formData.periodEnd
      });
      setRows((current) => [created, ...current]);
      setMessage("Statement generated through the backend.");
      setFormData(initialForm);
    } catch (submitError) {
      setError(submitError.message || "Statement generation failed.");
    } finally {
      setLoading(false);
    }
  }

  async function handleClose(statementId) {
    setLoading(true);
    setError("");
    setMessage("");

    try {
      const updated = await operationsApi.closeStatement(statementId);
      setRows((current) =>
        current.map((row) => (row.statementId === statementId ? { ...row, ...updated } : row))
      );
      setMessage(`Statement ${statementId} closed through the backend.`);
    } catch (submitError) {
      setError(submitError.message || "Unable to close the statement.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Layout section="operations" title="Operations Dashboard">
      <PageHeader
        title="Statements List Page"
        subtitle="Generate monthly statements and monitor due or overdue accounts."
      />

      <div className="card border-0 shadow-sm mb-4">
        <div className="card-body p-4">
          <form onSubmit={handleGenerate} className="row g-3">
            <div className="col-md-4">
              <label className="form-label">Account</label>
              <select className="form-select" name="accountId" value={formData.accountId} onChange={handleChange}>
                {accounts.map((account) => (
                  <option key={account.accountId} value={account.accountId}>
                    {account.customerEmail} - {account.accountId}
                  </option>
                ))}
              </select>
            </div>
            <div className="col-md-3">
              <label className="form-label">Period Start</label>
              <input type="date" className="form-control" name="periodStart" value={formData.periodStart} onChange={handleChange} required />
            </div>
            <div className="col-md-3">
              <label className="form-label">Period End</label>
              <input type="date" className="form-control" name="periodEnd" value={formData.periodEnd} onChange={handleChange} required />
            </div>
            <div className="col-md-2 d-flex align-items-end">
              <button className="btn btn-primary w-100" disabled={loading}>
                {loading ? "Saving..." : "Generate"}
              </button>
            </div>
          </form>
          {message ? <div className="alert alert-success mt-4 mb-0">{message}</div> : null}
          {error ? <div className="alert alert-danger mt-4 mb-0">{error}</div> : null}
        </div>
      </div>

      <div className="card border-0 shadow-sm">
        <div className="card-body">
          <DataTable
            columns={[
              { key: "statementId", label: "Statement ID" },
              { key: "accountId", label: "Account ID" },
              { key: "customerName", label: "Customer" },
              { key: "customerEmail", label: "Customer Email" },
              { key: "totalDue", label: "Total Due" },
              { key: "minimumDue", label: "Minimum Due" },
              { key: "generatedDate", label: "Generated Date" },
              { key: "status", label: "Status", type: "status" },
              {
                key: "actions",
                label: "Actions",
                render: (row) => (
                  <button
                    className="btn btn-sm btn-outline-danger"
                    disabled={loading || row.status !== "OPEN"}
                    onClick={() => handleClose(row.statementId)}
                  >
                    Close
                  </button>
                )
              }
            ]}
            rows={rows}
          />
        </div>
      </div>
    </Layout>
  );
}

export default StatementsListPage;
